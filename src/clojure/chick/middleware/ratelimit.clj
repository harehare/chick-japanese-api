(ns chick.middleware.ratelimit
  (:require
   [ring.util.http-response :refer [too-many-requests!]]
   [ring.middleware.ratelimit :as rl]))

(defn err-handler [req]
  (too-many-requests! {:status_code 429 :message "Too may request" :type :error}))

(def default-limit
  {:limits [(rl/ip-limit 1000)]
   :err-handler err-handler})

(defn ratelimit-mw
  [handler req]
  (let [response ((rl/wrap-ratelimit handler default-limit) req)]
    (update-in handler [:headers] merge (response :headers))))
