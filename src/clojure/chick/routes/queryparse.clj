(ns chick.routes.queryparse
  (:require [chick.services.queryparse :refer [response]]
            [chick.middleware.header :refer [header-mw]]
            [chick.middleware.ratelimit :refer [ratelimit-mw]]
            [chick.middleware.logging :refer [logging-mw]]
            [compojure.api.sweet :refer [context GET]]))

(def query-parse-routes
  (context "/api/queryparse" []
    :tags ["api/queryparse"]
    (GET "/" req
      :query-params [q]
      (-> (logging-mw #(response q) req)
          header-mw
          (ratelimit-mw req)))))