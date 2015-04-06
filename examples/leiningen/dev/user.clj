(ns user
  (:require 
   [reloaded.repl :refer [system init start stop go reset]]
   [example.systems :refer [dev-system]]))

(reloaded.repl/set-init! dev-system)
; type (go) in the repl to start your development-time system.
