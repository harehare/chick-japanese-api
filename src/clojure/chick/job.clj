(ns chick.job
  (:require [integrant.core :as ig]
            [clojure.core.async :as a]
            [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [chick.sql :as sql]
            [chick.db :refer [db]]
            [clojure.tools.logging :as log]
            [clojure.java.jdbc :as j]
            [clj-time.periodic :as p]))

(defmethod ig/init-key :chick/job-channel [_ _]
  (chime-ch (p/periodic-seq (t/now) (t/minutes 2))
            {:ch (a/chan (a/dropping-buffer 1))}))

(defmethod ig/halt-key! :chick/job-channel [_ ch]
  (a/close! ch))

(defmethod ig/init-key :chick/calc-tf-idf-job [_ {:keys [ch]}]
  (a/go-loop []
    (when-let [time (a/<! ch)]
      (j/with-db-transaction [tx db]
        (sql/truncate-tf-idf tx)
        (sql/calc-tf-idf tx))
      (log/info "TF-IDF updated.")
      (recur))))
