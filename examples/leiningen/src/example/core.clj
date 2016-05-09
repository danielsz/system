(ns example.core
  (:gen-class)
  (:require 
   [system.repl :refer [system init start stop reset]]
   [example.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (init prod-system)
  (start))
