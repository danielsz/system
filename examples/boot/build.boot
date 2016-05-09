(set-env!
 :resource-paths #{"src"}
 :dependencies '[[tolitius/boot-check "0.1.2-SNAPSHOT" :scope "test"]

                 [org.danielsz/system "0.3.0-SNAPSHOT"]
                 [environ "1.0.2"]
                 [boot-environ "1.0.2"]

                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]

                 [org.clojure/tools.nrepl "0.2.12"]])

(require
 '[tolitius.boot-check :as check]
 '[environ.boot :refer [environ]]
 '[example.systems :refer [dev-system]]
 '[system.boot :refer [system run]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "3000"})
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["handler.clj" "html.clj"])
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port "3000"})
   (run :main-namespace "example.core" :arguments ['dev-system])
   (wait)))

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace '#{example.core})
   (pom :project 'myproject
        :version "1.0.0")
   (uber)
   (jar :main 'example.core)))
