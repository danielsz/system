(ns system.components.immutant-web-test
  (:require
   [system.components.immutant-web :refer [new-web-server]]
   system.monitoring.immutant-web
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]
   [clojure.test :refer [testing deftest is]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def http-server (new-web-server 8081 handler))

(deftest http-server-lifecycle
  (alter-var-root #'http-server component/start)
  (is (:server http-server) "HTTP server has been added to component")
  (alter-var-root #'http-server component/stop))

(deftest http-server-illegal-port-throws
  (is (thrown? RuntimeException (new-web-server -1 handler))))

(deftest http-server-illegal-dispatch-option-throws
  (is (thrown? RuntimeException (new-web-server 8080 handler {:dispatch? "bob"}))))

(deftest http-server-monitoring-status
  (alter-var-root #'http-server component/start)
  (is (= (monitoring/status http-server) :running))
  (alter-var-root #'http-server component/stop)
  (is (= (monitoring/status http-server) :down)))
