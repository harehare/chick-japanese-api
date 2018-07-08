(ns chick.queryparse-test
  (:require [clojure.test :refer :all]
            [chick.services.queryparse :refer :all]))

(deftest
  response-test
  (testing "response-tokenize"
    (are [x y] (= y (response x))
      "テスト" {:status 200, :headers {}, :body "[\"\\u30c6\\u30b9\\u30c8\"]"}
      "California" {:status 200, :headers {}, :body "[\"california\"]"})))