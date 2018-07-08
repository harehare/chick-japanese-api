(ns chick.routes.scraping
  (:require [chick.services.scraping :refer [response]]
            [chick.validate :as v]
            [clojure.string :as str]
            [chick.middleware.cors :refer [cors-mw]]
            [chick.middleware.ratelimit :refer [ratelimit-mw]]
            [ring.util.http-response :refer [bad-request]]
            [chick.middleware.logging :refer [logging-mw]]
            [compojure.api.sweet :refer [context routes GET]]))

(def scraping-routes
  (context "/api/scraping" []
    :tags ["api/scraping"]
    (GET "/" req
      :query-params [urls]
      (let [url-items (str/split urls #",")]
        (->
         (logging-mw #(response url-items) req)
         cors-mw
         (ratelimit-mw req))))))