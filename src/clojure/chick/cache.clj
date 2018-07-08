(ns chick.cache
  (:require
   [taoensso.carmine :as car :refer [wcar]]
   [environ.core :refer [env]]))

(def conn {:pool {} :spec {:uri (:redis-url env)}})

(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn get-from-cache
  [key f]
  (let [hashkey (hash key)
        cache (wcar* (car/get hashkey))]
    (or
     cache
     (let [result (f)]
       (wcar* (car/set hashkey result))
       (wcar* (car/expire hashkey 600))
       result))))
