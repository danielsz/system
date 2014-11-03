(defproject org.danielsz/system "0.1.1"
  :description "Reloaded components à la carte"
  :url "https://github.com/danielsz/system"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.19"]
                 [org.clojure/tools.nrepl "0.2.6"]
                 [ring "1.3.1"]
                 [im.chit/hara.io.watch "2.1.7"]
                 [com.datomic/datomic-free "0.9.4815.12"]
                 [com.novemberain/monger "2.0.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [com.h2database/h2 "1.4.181"]
                 [com.novemberain/langohr "2.11.0"]
                 [com.taoensso/sente "1.0.0"]
                 [org.danielsz/etsy "0.1.2" ]]
  :profiles {:dev {:dependencies [[reloaded.repl "0.1.0"]]
                    :plugins     [[lein-repack "0.2.4"]]}}
  :repack [{:type :clojure
            :path "src"
            :levels 2}]
  :scm {:name "git"
        :url "https://github.com/danielsz/system"})
