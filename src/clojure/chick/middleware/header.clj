(ns chick.middleware.header)

(defn header-mw
  [handler]
  (-> handler
      (assoc-in [:headers "Content-Type"] "application/json")
      (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
      (assoc-in [:headers "Access-Control-Allow-Methods"] "GET")
      (assoc-in [:headers "Access-Control-Allow-Headers"] "Authorization, Content-Type")))