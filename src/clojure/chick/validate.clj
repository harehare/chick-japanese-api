(ns chick.validate
  (:import org.chick.exception.InvalidParameterError))

(defn required
  [v]
  (when (if (string? v)
          (empty? v)
          (nil? v))
    (throw (InvalidParameterError. "Required."))))

(defn url
  [url]
  (.matches (re-matcher #"^(http|https)://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$" url)))

(defn urls
  [urls]
  (doseq [u urls]
    (when-not (url u)
      (throw (InvalidParameterError. (str "Invalid url " u))))))