(ns system.components.quartzite-test
  (:require
   [system.components.quartzite :as quartz]
   system.monitoring.quartzite
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]
   [clojurewerkz.quartzite.scheduler :as qs]
   [clojure.test :refer [deftest is]]))

(def scheduler (quartz/new-scheduler))

(deftest quartzite-test
  (alter-var-root #'scheduler component/start)
  (is (qs/started? (:scheduler scheduler)) "the scheduler is running")
  (alter-var-root #'scheduler component/stop)
  (is (qs/shutdown? (:scheduler scheduler)) "the scheduler is stopped"))


(deftest quartzite-monitoring-status
  (alter-var-root #'scheduler component/start)
  (is (= (monitoring/status scheduler) :running))
  (alter-var-root #'scheduler component/stop)
  (is (= (monitoring/status scheduler) :down)))
