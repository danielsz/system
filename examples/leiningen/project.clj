(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]
                 [org.danielsz/system "0.4.1"]
                 [environ "1.0.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :env {:http-port 3000}}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    :dependencies [[org.clojure/tools.nrepl "0.2.12"]]}
             :uberjar {:aot :all}}
  :main ^:skip-aot example.core
  :target-path "target/%s")
