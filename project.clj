(defproject org.danielsz/system "0.3.0-SNAPSHOT"
  :description "Reloaded components à la carte"
  :url "https://github.com/danielsz/system"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reloaded.repl "0.1.0"]
                 [org.clojure/tools.namespace "0.3.0-alpha3"]
                 [com.stuartsierra/component "0.2.3"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.nrepl "0.2.11"]
                                  [cider/cider-nrepl "0.10.1"]
                                  [ring "1.4.0"]
                                  [im.chit/hara.io.watch "2.1.7"]
                                  [com.datomic/datomic-free "0.9.4815.12"]
                                  [com.novemberain/monger "2.0.1"]
                                  [org.clojure/java.jdbc "0.3.5"]
                                  [com.h2database/h2 "1.4.181"]
                                  [org.postgresql/postgresql "9.4-1201-jdbc41"]
                                  [com.novemberain/langohr "2.11.0"]
                                  [clojurewerkz/neocons "3.0.0"]
                                  [clojurewerkz/quartzite "2.0.0"]
                                  [com.taoensso/sente "1.6.0"]
                                  [org.danielsz/etsy "0.1.2" ]
                                  [http-kit "2.1.19"]
                                  [org.immutant/web "2.1.2"]
                                  [org.elasticsearch/elasticsearch "1.6.0"
                                   :exclusions [org.antlr/antlr-runtime
                                                org.apache.lucene/lucene-analyzers-common
                                                org.apache.lucene/lucene-grouping
                                                org.apache.lucene/lucene-highlighter
                                                org.apache.lucene/lucene-join
                                                org.apache.lucene/lucene-memory
                                                org.apache.lucene/lucene-misc
                                                org.apache.lucene/lucene-queries
                                                org.apache.lucene/lucene-queryparser
                                                org.apache.lucene/lucene-sandbox
                                                org.apache.lucene/lucene-spatial
                                                org.apache.lucene/lucene-suggest
                                                org.ow2.asm/asm
                                                org.ow2.asm/asm-commons]]
                                  [aleph "0.4.0-alpha9"]]}}
  :scm {:name "git"
        :url "https://github.com/tracksun/system"})
