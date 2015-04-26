(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.danielsz/system "0.1.5-SNAPSHOT"]
                 [ring/ring-defaults "0.1.4"]
                 [ring "1.3.2"]
                 [environ "1.0.0"]
                 [compojure "1.3.3"]
                 [danielsz/boot-environ "0.0.1"]
                 [org.clojure/tools.nrepl "0.2.10"]])

(require
 '[reloaded.repl :as repl :refer [start stop go reset]]
 '[example.systems :refer [dev-system]]
 '[danielsz.boot-environ :refer [environ]]
 '[system.boot :refer [system]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port 3000})
   (watch :verbose true)
   (system :sys #'dev-system)
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port 3000})
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
