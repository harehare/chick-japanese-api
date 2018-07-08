(ns chick.sql
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "chick/sql/words.sql")
