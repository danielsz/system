(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.danielsz/system "0.4.0"]

                 [environ "1.1.0"]
                 [boot-environ "1.1.0"]

                 [ring "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [compojure "1.6.0"]

                 [org.clojure/tools.nrepl "0.2.12"]])

(require
 '[environ.boot :refer [environ]]
 '[example.systems :refer [dev-system]]
 '[system.boot :refer [system run]]
 '[system.repl :refer [go reset]])

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
   (run :main-namespace "example.core" :arguments [#'dev-system])
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
