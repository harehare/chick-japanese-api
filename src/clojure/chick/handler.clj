(ns chick.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :as response]
            [clojure.core.async :refer [chan]]
            [compojure.api.exception :as ex]
            [compojure.route :as route]
            [integrant.core :as ig]
            [chick.routes.scraping :refer [scraping-routes]]
            [chick.routes.queryparse :refer [query-parse-routes]]
            [chick.routes.scoring :refer [scoring-routes]]))

(defn api-error-handler [f type]
  (fn [^Exception e data request]
    (.printStackTrace e)
    (f {:status_code 500 :message (.getMessage e), :type type})))

(def app
  (api
   {:exceptions
    {:handlers
     {::ex/default (api-error-handler response/internal-server-error :unknown)}}
    :swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "chick-japanese-api "
                   :description "Chick api example"}
            :tags [{:name "api", :description "chick apis"}]}}}
   scraping-routes
   query-parse-routes
   scoring-routes
   (undocumented
    (route/not-found (response/not-found {:status_code "404" :message "Not Found"})))))

(defmethod ig/init-key :handler/app [_ _] app)