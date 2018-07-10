(ns chick.routes.scoring
  (:import org.chick.exception.InvalidParameterError)
  (:require [chick.services.scoring :refer [response]]
            [clojure.string :as str]
            [chick.validate :as v]
            [ring.util.http-response :refer [bad-request]]
            [chick.middleware.header :refer [header-mw]]
            [chick.middleware.ratelimit :refer [ratelimit-mw]]
            [chick.middleware.logging :refer [logging-mw]]
            [compojure.api.sweet :refer [context routes POST]]))

(def scoring-routes
  (context "/api/scoring" []
    :tags ["api/scoring"]
    (POST "/" req
      :body-params [urls tokens]
      (try
        (v/required tokens)
        (->
         (logging-mw #(response tokens urls) req)
         header-mw
         (ratelimit-mw req))
        (catch InvalidParameterError e (bad-request {:status_code 400 :message "invalid parameter." :type :validation}))))))
