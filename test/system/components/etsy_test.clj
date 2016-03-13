(ns system.components.etsy-test
  (:require
   [system.components.etsy :refer [new-etsy-client]]
   system.monitoring.etsy
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]
   [clojure.test :refer [deftest is]]))

(def etsy-client (new-etsy-client "123" "1234"))

(deftest etsy-client-test
  (alter-var-root #'etsy-client component/start)
  (is (:client etsy-client) "a client is present")
  (alter-var-root #'etsy-client component/stop))

(deftest etsy-client-monitoring-status
  (alter-var-root #'etsy-client component/start)
  (is (= (monitoring/status etsy-client) :running))
  (alter-var-root #'etsy-client component/stop)
  (is (= (monitoring/status etsy-client) :down)))
