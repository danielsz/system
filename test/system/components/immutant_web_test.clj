(ns system.components.immutant-web-test
  (:require [system.components.immutant-web :refer [new-web-server new-immutant-web]]
            [com.stuartsierra.component :as component]
            [clj-http.client :as client]
            [clojure.test :refer [testing deftest is]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn handler2 [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello Universe"})

(def http-server (new-web-server 8081 handler))

(deftest http-server-lifecycle
  (alter-var-root #'http-server component/start)
  (is (:server http-server) "HTTP server has been added to component")
  (alter-var-root #'http-server component/stop)
  (is (= system.components.immutant_web.WebServer (type http-server))))

(deftest http-server-illegal-port-throws
  (is (thrown? RuntimeException (new-web-server -1 handler))))

(deftest http-server-illegal-dispatch-option-throws
  (is (thrown? RuntimeException (new-web-server 8080 handler {:dispatch? "bob"}))))

(deftest one-handler
  (let [server (component/start (new-immutant-web :port 8083 :handler handler))]
    (is (= "Hello World" (:body  (client/get "http://localhost:8083"))))
    (component/stop server)))

(deftest two-handlers
  (let [server (component/start (new-immutant-web :port 8083 :handler handler))
        server (component/start (new-immutant-web :port 8083 :handler handler2 :options {:path "second"}))]
    (is (= "Hello World" (:body  (client/get "http://localhost:8083"))))
    (is (= "Hello Universe" (:body (client/get "http://localhost:8083/second"))))
    (component/stop server)))
