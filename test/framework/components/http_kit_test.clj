(ns framework.components.http-kit-test
  (:require [framework.components.http-kit :refer [new-web-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [testing deftest is]]
   [ring.mock.request :refer :all]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def http-server (new-web-server 8081 handler))
 
(deftest http-server-lifecycle
  (alter-var-root #'http-server component/start)
  (is (:server http-server) "HTTP server has been added to component")
  (alter-var-root #'http-server component/stop))


