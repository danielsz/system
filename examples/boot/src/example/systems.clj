(ns example.systems
  (:require [system.core :refer [defsystem]]
            (system.components 
             [jetty :refer [new-web-server]]
             [repl-server :refer [new-repl-server]])
            [environ.core :refer [env]]
            [example.handler :refer [app routes]]))

(defsystem dev-system
  [:web (new-web-server (Integer. (env :http-port)) (var routes))])

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))])
