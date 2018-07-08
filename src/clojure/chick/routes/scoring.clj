(ns chick.routes.scoring
  (:import org.chick.exception.InvalidParameterError)
  (:require [chick.services.scoring :refer [response]]
            [clojure.string :as str]
            [chick.validate :as v]
            [ring.util.http-response :refer [bad-request]]
            [chick.middleware.cors :refer [cors-mw]]
            [chick.middleware.ratelimit :refer [ratelimit-mw]]
            [chick.middleware.logging :refer [logging-mw]]
            [compojure.api.sweet :refer [context routes GET]]))

(def scoring-routes
  (context "/api/scoring" []
    :tags ["api/scoring"]
    (GET "/" req
      :query-params [tokens urls]
      (try
        (let [token-items (str/split tokens #",")
              url-items (str/split urls #",")]
          (v/required token-items)
          (->
           (logging-mw #(response token-items url-items) req)
           cors-mw
           (ratelimit-mw req)))
        (catch InvalidParameterError e (bad-request {:status_code 400 :message "invalid parameter." :type :validation}))))))
