(ns user
  (:require 
   [system.repl :refer [system init start stop reset]]
   [example.systems :refer [dev-system]]))

(set-init! #'dev-system)
; type (start) in the repl to start your development-time system.
