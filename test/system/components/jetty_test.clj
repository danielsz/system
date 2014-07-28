(ns system.components.jetty-test
  (:require [system.components.jetty :refer [new-web-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [testing deftest is]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def http-server (new-web-server 8081 handler))
 
(deftest http-server-lifecycle
  (alter-var-root #'http-server component/start)
  (is (:server http-server) "HTTP server has been added to component")
  (is (.isStarted (:server http-server)) "HTTP server starts")
  (alter-var-root #'http-server component/stop)
  (is (.isStopped (:server http-server)) "HTTP server stops"))


