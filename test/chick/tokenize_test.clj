(ns chick.tokenize-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [chick.morpheme.tokenize :refer :all]))

(deftest
  char-filter-test
  (testing "char-filter"
    (are [x y] (= y (char-filter x))
      ["1"] []
      ["ABCD"] ["abcd"]
      ["テスト"] ["テスト"])))

(deftest
  analyze-ngram-test
  (testing "analyze-ngram"
    (are [x y] (= y (analyze-ngram x))
      "1" []
      "ab" ["ab"]
      "abc" ["bc", "ab"])))

(deftest
  analyze-test
  (testing "analyze"
    (are [x y] (= y (analyze x))
      "です" []
      "about" []
      "テストです" ["テスト"]
      "テスト。" ["テスト"]
      "About" []
      "the" [])))