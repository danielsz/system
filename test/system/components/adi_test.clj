(ns system.components.adi-test
  (:require [adi.core :as adi]
            [system.components.adi :refer [new-adi-db]]
            system.monitoring.adi
            [com.stuartsierra.component :as component]
            [system.monitoring.monitoring :as monitoring]
            [clojure.test :refer [deftest is]]))

(def db-schema {:person/name [{:type :string}]})
(def adi-db (new-adi-db "datomic:mem://system-adi-test" db-schema true true))

(deftest start-adi
  (alter-var-root #'adi-db component/start)
  (is (= (type (:connection adi-db)) datomic.peer.LocalConnection))
  (alter-var-root #'adi-db component/stop))

(deftest start-adi-idempotent
  (alter-var-root #'adi-db component/start)
  (let [connection (:connection adi-db)]
    (alter-var-root #'adi-db component/start)
    (is (identical? connection (:connection adi-db))))
  (alter-var-root #'adi-db component/stop))

(deftest stop-adi
  (alter-var-root #'adi-db component/start)
  (alter-var-root #'adi-db component/stop)
  (is (nil? (:connection adi-db))))

(deftest adi-monitoring-status
  (alter-var-root #'adi-db component/start)
  (is (= (monitoring/status adi-db) :running))
  (alter-var-root #'adi-db component/stop)
  (is (= (monitoring/status adi-db) :down)))
