(defproject org.danielsz/framework "0.1.0-SNAPSHOT"
  :description "TODO"
  :url "TODO"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.0"]
                 [environ "0.5.0"]
                 [org.clojure/tools.nrepl "0.2.3"]
                 [reloaded.repl "0.1.0"]
                 [com.stuartsierra/component "0.2.1"]
                 [com.datomic/datomic-free "0.9.4609"]
                 [com.novemberain/monger "2.0.0"]
                 [com.novemberain/langohr "2.11.0"]
                 [com.taoensso/sente "0.15.1"]
                 [http-kit "2.1.18"]]
  :plugins [[lein-environ "0.5.0"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]
                                  [compojure "1.1.8"]]
                   :source-paths ["dev"]
                   :env {:http-port 3000 :trace-headers true
                         :db-url "datomic:mem://localhost:4334/framework"}}
             :production {:env {:http-port 8000 :trace-headers false
                                :repl-port 8001
                                :db-url "datomic:free://localhost:4334/framework"
                                :mongo-url "mongodb://heroku_url"}}}
  :repositories [["snapshots" {:url "https://clojars.org/" :creds :gpg}]
                 ["releases" {:url "https://clojars.org/" :creds :gpg}]]
  :scm {:name "git"
        :url "https://github.com/danielsz/framework"})
