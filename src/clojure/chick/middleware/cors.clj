(ns chick.middleware.cors)

(defn cors-mw
  [handler]
  (-> handler
      (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
      (assoc-in [:headers "Access-Control-Allow-Methods"] "GET")
      (assoc-in [:headers "Access-Control-Allow-Headers"] "Authorization, Content-Type")))