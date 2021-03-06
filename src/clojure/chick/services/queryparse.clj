(ns chick.services.queryparse
  (:require [clojure.data.json :as json]
            [ring.util.http-response :refer :all]
            [chick.cache :refer [get-from-cache]]
            [chick.morpheme.tokenize :refer [analyze analyze-ngram]]))

(defn response
  [q]
  (ok (json/write-str (get-from-cache q #(concat (analyze q) (analyze-ngram q))))))
