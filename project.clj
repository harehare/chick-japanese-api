(defproject chick "0.1.0-SNAPSHOT"
  :description "Japanese morphological analysis API for Chick"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :ring {:handler chick.handler/app :async? true}
  :uberjar-name "chick.jar"
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :min-lein-version "2.7.1"
  :migratus {:store :database
             :migration-dir "migrations"
             :db ~(get (System/getenv) "JDBC_DATABASE_URL")}
  :dependencies [[com.atilika.kuromoji/kuromoji-ipadic "0.9.0"]
                 [com.layerware/hugsql "0.4.9"]
                 [org.clojure/clojure "1.9.0"]
                 [org.jsoup/jsoup "1.11.3"]
                 [org.clojure/java.jdbc "0.7.7"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.slf4j/slf4j-api "1.7.25"]
                 [metosin/compojure-api "2.0.0-alpha20"]
                 [ring/ring-core "1.7.0-RC1"]
                 [ring/ring-jetty-adapter "1.7.0-RC1"]
                 [ring-ratelimit "0.2.2"]
                 [migratus "1.0.6"]
                 [org.postgresql/postgresql "42.2.2"]
                 [com.taoensso/carmine "2.18.1"]
                 [integrant "0.6.3"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [compojure "1.6.1"]
                 [environ "1.1.0"]
                 [clj-http "3.9.0"]
                 [jarohen/chime "0.2.2"]]
  :profiles {:uberjar {:main chick.core :aot :all}
             :precomp {:aot :all}
             :dev {:dependencies [[integrant/repl "0.2.0"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [cljfmt "0.5.7"]]
                   :env {:jdbc-database-url "jdbc:postgresql://localhost:5432/chick?user=chick&password=chick"
                         :redis-url "redis://localhost:6379"
                         :port "8080"}
                   :global-vars {*warn-on-reflection* true}
                   :plugins [[cider/cider-nrepl "0.17.0"]
                             [migratus-lein "0.5.7"]
                             [lein-kibit "0.1.6"]
                             [lein-ring "0.12.0"]
                             [jonase/eastwood "0.2.7"]
                             [lein-environ "1.1.0"]]
                   :source-paths ["env/dev/clj"]}})
