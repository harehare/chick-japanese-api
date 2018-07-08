(ns chick.db
  (:require
   [environ.core :refer [env]]))

(def db
  {:connection-uri (:jdbc-database-url env)})