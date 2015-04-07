(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.danielsz/system "0.1.4"]
                 [ring/ring-defaults "0.1.4"]
                 [ring "1.3.2"]
                 [environ "1.0.0"]
                 [compojure "1.3.3"]
                 [danielsz/boot-environ "0.0.1"]
                 [org.clojure/tools.nrepl "0.2.10"]])

(require
 '[reloaded.repl :refer [system init start stop go reset]]
 '[example.systems :refer [dev-system]]
 '[danielsz.boot-environ :refer [environ]])

(reloaded.repl/set-init! dev-system)

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace '#{example.core})
   (pom :project 'myproject
        :version "1.0.0")
   (uber)
   (jar :main 'example.core)))
