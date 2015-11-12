(ns system.components.quartzite-test
  (:require [clojure.test :refer [deftest is]]
            [clojurewerkz.quartzite.scheduler :as qs]
            [com.stuartsierra.component :as component]
            [system.components.quartzite :as quartz]))

(def scheduler (quartz/new-scheduler))

(deftest quartzite-test
  (alter-var-root #'scheduler component/start)
  (is (qs/started? (:scheduler scheduler)) "the scheduler is running")
  (alter-var-root #'scheduler component/stop)
  (is (qs/shutdown? (:scheduler scheduler)) "the scheduler is stopped"))
