(ns framework.components.jetty-test
  (:require [framework.components.jetty :refer [new-web-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is]]
   [ring.mock.request :refer :all]))


(def http-server (new-web-server 8081 false))
 
(deftest http-server-lifecycle
  (alter-var-root #'http-server component/start)
  (is (:server http-server) "HTTP server has been added to component")
  (is (.isStarted (:server http-server)) "HTTP server starts")
  (alter-var-root #'http-server component/stop)
  (is (.isStopped (:server http-server)) "HTTP server stops"))


(deftest request-ok
  (testing "get /bar returns succesfully"
    (is (= (select-keys (app (request :get "/bar")) [:status]) 
        {:status 200}))))

(deftest request-not-ok
  (testing "get /foo doesn't return succesfully"
    (is (not= (select-keys (app (request :get "/foo")) [:status]) 
        {:status 200}))))
