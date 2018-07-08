(ns chick.services.scoring
  (:require [clojure.data.json :as json]
            [ring.util.http-response :refer :all]
            [chick.sql :as sql]
            [chick.cache :refer [get-from-cache]]
            [chick.db :refer [db]]))

(defn scoring
  [tokens urls]
  (sql/get-score db {:tokens tokens :urls urls}))

(defn response
  [token-items url-items]
  (ok (json/write-str (get-from-cache (str token-items "#" url-items) #(scoring token-items url-items)))))
