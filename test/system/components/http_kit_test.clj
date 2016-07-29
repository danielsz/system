(ns system.components.http-kit-test
  (:require [system.components.http-kit :refer [new-web-server]]
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
  (alter-var-root #'http-server component/stop)
  (is (= system.components.http_kit.WebServer (type http-server))))

(deftest http-server-options-invalid-key-throws
  (is (thrown? RuntimeException (new-web-server 8080 handler {:threads 7}))))

(deftest http-server-options-invalid-value-throws
  (is (thrown? RuntimeException (new-web-server 8080 handler {:thread 7.7}))))

(deftest http-server-options-invalid-port-throws
  (is (thrown? RuntimeException (new-web-server -1 handler {:thread 7}))))



