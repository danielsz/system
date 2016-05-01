(ns example.core
  (:gen-class)
  (:require 
   [system.repl :refer [init start stop reset]]
   [example.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (let [system (or (first args) #'prod-system)]
    (init system)
    (start)))
