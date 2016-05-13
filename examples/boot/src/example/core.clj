(ns example.core
  (:gen-class)
  (:require 
   [system.repl :refer [set-init! start]]
   [example.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (let [system (or (first args) #'prod-system)]
    (set-init! system)
    (start)))
