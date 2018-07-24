(ns chick.services.scraping
  (:import org.jsoup.Jsoup)
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.core.async :as a]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]
            [chick.sql :as sql]
            [clojure.java.jdbc :as j]
            [chick.morpheme.tokenize :refer [analyze, analyze-ngram]]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [chick.db :refer [db]]))

(defonce text-queue (a/chan 10))

(defonce result-queue (a/chan 10))

(defonce insert-queue (a/chan 100))

(defonce cpu-num (inc (.availableProcessors (Runtime/getRuntime))))

(defn exponential-backoff [wait-time rate max-time f]
  (if (<= wait-time max-time)
    (try
      (f)
      (catch Throwable t
        (Thread/sleep wait-time)
        (log/error (str "fetch error.exponential backoff" (* wait-time rate)))
        (exponential-backoff (* wait-time rate) rate max-time f)))
    ""))

(defn fetch-http
  [^String url]
  (log/debug (str "fetch " url))
  (exponential-backoff 1000 3 5000 #((client/get url) :body)))

(defn strip-html-tags
  [s]
  (if (empty? s)
    (throw (Exception. "Html is empty."))
    (.text (Jsoup/parse s))))

(a/pipeline
 cpu-num
 result-queue
 (map (fn [m] (let [{:keys [url html]} m
                    text (strip-html-tags html)
                    tokens (analyze text)
                    all-tokens (concat tokens (analyze-ngram text))
                    snippet (subs text 0 (min 200 (count text)))]
                (log/debug (str "tokens " (count tokens)))
                (a/go (a/>! insert-queue {:tokens tokens :url url}))
                {:tokens all-tokens
                 :url url
                 :status_code 200
                 :snippet snippet})))
 text-queue
 false
 (fn [^Exception error] (log/error error)))

(a/go-loop []
  (when-let [{:keys [tokens url]} (a/<! insert-queue)]
    (j/with-db-transaction [tx db]
      (doseq [token tokens]
        (sql/new-words tx {:id (str token "#" url) :word token :url url})))
    (log/info "Word info updated.")
    (recur)))

(defn result
  [url-items]
  (let [res (atom [])]
    (doseq [v (range 0 (count url-items))]
      (swap! res conj (future (a/<!! result-queue))))
    (json/write-str (map deref (deref res)))))

(defn response
  [url-items]
  (doseq [url url-items]
    (a/thread
      (try
        (let [html (fetch-http url)]
          (a/go (a/>! text-queue {:html html :url url})))
        (catch Exception e (a/go (a/>! result-queue {:status_code 500 :url url :tokens [] :snippet ""}))))))
  (ok (result url-items)))