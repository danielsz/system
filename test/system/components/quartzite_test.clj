(ns system.components.quartzite-test
  (:require
   [com.stuartsierra.component :as component]
   [system.components.quartzite :as quartz]
   (system.monitoring quartzite
                      [core :refer [started? stopped?]])
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
  (is (started? scheduler))
  (alter-var-root #'scheduler component/stop)
  (is (stopped? scheduler)))
