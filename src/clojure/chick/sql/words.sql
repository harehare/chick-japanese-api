-- :name truncate-tf-idf :! :n
TRUNCATE words_tfidf;

-- :name calc-tf-idf :! :n
-- :doc insert new words 
INSERT INTO words_tfidf (id, word, url, tfidf)
SELECT
    words.id,
    words.word, 
    words.url,
    (CAST(count(words.word) AS REAL) / CAST(SUM(total.word_count) AS REAL)) * SUM(idf.idf) AS tfidf
FROM
    words 
    INNER JOIN
        (SELECT url, count(word) AS word_count from words GROUP BY url) AS total
    ON words.url = total.url
    INNER JOIN
        (SELECT LOG((SELECT CAST(COUNT(*) AS REAL) FROM words) / CAST(count(url) AS REAL)) AS idf, word FROM words GROUP BY word) AS idf
    ON idf.word = words.word
GROUP BY words.id, words.url, words.word;

-- :name new-words :! :n
-- :doc insert new words 
INSERT INTO words (id, word, url)
    VALUES (:id, :word, :url) ON CONFLICT (id)
DO UPDATE
    SET word = :word,
        url = :url;

-- :name get-score :* :*
-- :doc get score for words
SELECT
    url, word, tfidf AS score 
FROM
    words_tfidf
WHERE
    url in (:v*:urls) AND word in (:v*:tokens)
