(ns chick.morpheme.tokenize
  (:import com.atilika.kuromoji.ipadic.Tokenizer)
  (:import com.atilika.kuromoji.ipadic.Token)
  (:import java.text.Normalizer)
  (:require [clojure.string :as str]
            [clojure.java.io :refer :all]))

(def tokenizer (new Tokenizer))

(def stopwords (future (with-open [fin (reader "resources/stopwords/en.txt")]
                         (set (map identity (line-seq fin))))))

(defn tokenize
  [^String text]
  (let [xf (comp
            (filter (fn [^Token s] (= "名詞" (first (.getAllFeaturesArray s)))))
            (map (fn [^Token s] (Normalizer/normalize (.getSurface s) java.text.Normalizer$Form/NFKC)))
            (map (fn [^String s] (str/replace s #"[ ・〜ー一①-⑨【】、。!#$%&\"'()*+-\.,/:;<=>?@\[\]^_`{|}~]" "")))
            (map (fn [^String s] (str/trim s)))
            (filter (fn [^String s] (> (.length s) 2))))]
    (sequence xf (.tokenize ^Tokenizer tokenizer (str/replace text #"\n" " ")))))

(defn bigram-tokenize
  [^String text]
  (loop [text text
         result []]
    (if (< (count text) 2)
      result
      (recur (subs text 1) (cons (subs text 0 2) result)))))

(defn char-filter
  [tokens]
  (let [xf (comp
            (filter (fn [s] (not (.matches (re-matcher #"^\d+$|.* .*|.*[ 〜ー一①-⑨【】、。!\"#$%&'()*+-\.,/:;<=>?@\[\]^_`{|}~].*" s)))))
            (map (fn [s] (str/lower-case s)))
            (filter (fn [^String s] seq))
            (filter (fn [^String s] (not (contains? @stopwords s))))
            (distinct))]
    (sequence xf tokens)))

(defn analyze
  [^String text]
  (char-filter (tokenize text)))

(defn analyze-ngram
  [^String text]
  (char-filter (bigram-tokenize text)))
