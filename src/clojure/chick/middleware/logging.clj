(ns chick.middleware.logging
  (:require
   [clojure.string :refer [upper-case]]
   [clojure.tools.logging :as log]))

(defn logging-mw
  [handler req]
  (let [start (System/currentTimeMillis)
        {:keys [request-method uri remote-addr query-string]} req
        result (handler)
        finish (System/currentTimeMillis)]
    (log/info (str (name request-method) " " (- finish start) " " uri (if query-string (str "?" query-string)) " " remote-addr))
    result))
