(ns chick.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [chick.handler :as handler]
            [chick.job :as job]
            [chick.db :refer [db]]
            [migratus.core :as migratus]
            [integrant.core :as ig]))

(def config
  (ig/read-string (slurp "config.edn")))

(defmethod ig/init-key :chick/migrate-config [_ _]
  {:store :database
   :migration-dir "migrations"
   :db db})

(defmethod ig/init-key :chick/migrate [_ {:keys [config]}]
  (migratus/migrate config))

(defmethod ig/init-key :chick/port [_ _]
  (Integer/parseInt (env :port)))

(defmethod ig/init-key :adapter/jetty [_ opts]
  (let [handler (atom (delay (:handler opts)))
        options (-> opts (dissoc :handler) (assoc :join? false))]
    {:handler handler
     :server  (jetty/run-jetty @@handler options)}))

(defmethod ig/halt-key! :adapter/jetty [_ {:keys [server]}]
  (.stop server))

(defmethod ig/suspend-key! :adapter/jetty [_ {:keys [handler]}]
  (reset! handler (promise)))

(defmethod ig/resume-key :adapter/jetty [key opts old-opts old-impl]
  (if (= (dissoc opts :handler) (dissoc old-opts :handler))
    (do (deliver @(:handler old-impl) (:handler opts))
        old-impl)
    (do (ig/halt-key! key old-impl)
        (ig/init-key key opts))))

(defn -main
  []
  (ig/init config))