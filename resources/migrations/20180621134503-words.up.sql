CREATE TABLE IF NOT EXISTS words (
    id VARCHAR(255) PRIMARY KEY,
    word VARCHAR(255),
    url VARCHAR(255)
);
--;;
CREATE TABLE IF NOT EXISTS words_tfidf (
    id VARCHAR(255) PRIMARY KEY,
    word VARCHAR(255),
    url VARCHAR(255),
    tfidf REAL 
);
--;;
CREATE INDEX IF NOT EXISTS idx_url_word ON words_tfidf(url, word);