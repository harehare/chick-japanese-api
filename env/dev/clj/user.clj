(ns user
  (:require [integrant.repl :refer [clear go halt init reset reset-all prep]]
            [chick.core]
            [integrant.core :as ig]))

(integrant.repl/set-prep! (constantly (ig/read-string (slurp "config.edn"))))