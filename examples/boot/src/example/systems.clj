(ns example.systems
  (:require [system.core :refer [defsystem]]
            (system.components 
             [jetty :refer [new-web-server]]
             [repl-server :refer [new-repl-server]])
            [environ.core :refer [env]]
            [example.handler :refer [app]]))

(defsystem dev-system
  [:web (new-web-server (env :http-port) app)])

(defsystem prod-system
  [:web (new-web-server (env :http-port) app)
   :repl-server (new-repl-server (env :repl-port))])
