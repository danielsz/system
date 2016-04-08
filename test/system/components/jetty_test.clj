(ns system.components.jetty-test
  (:require
   [com.stuartsierra.component :as component]
   [system.components.jetty :refer [new-web-server]]
   (system.monitoring jetty
                      [core :refer [started? stopped?]])
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

(deftest http-server-valid-options-does-not-throw
  (is (new-web-server 8081 handler {:host "example.com"
                                    :configurator {}
                                    :join? false
                                    :daemon? true
                                    :ssl-port 8443
                                    :keystore "/tmp/keystore"
                                    :key-password "keeping"
                                    :truststore "/tmp/truststore"
                                    :trust-password "secrets"
                                    :max-threads 16
                                    :min-threads 2
                                    :max-idle-time 12345
                                    :client-auth {}
                                    :send-date-header? true
                                    :output-buffer-size 12345
                                    :request-header-size 12345
                                    :response-header-size 12345})))


(deftest http-server-port-too-low-throws
  (is (thrown? RuntimeException (new-web-server -1 handler))))

(deftest http-server-port-too-high-throws
  (is (thrown? RuntimeException (new-web-server 65536 handler))))

(deftest http-server-max-threads-too-low-throws
  (is (thrown? RuntimeException (new-web-server 8080 handler {:max-threads 0}))))

(deftest http-server-monitoring-status
  (alter-var-root #'http-server component/start)
  (is (started? http-server))
  (alter-var-root #'http-server component/stop)
  (is (stopped? http-server)))
