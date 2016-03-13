(ns system.components.h2-test
  (:use clojure.test)
  (:require
   [system.components.h2 :as h2]
   system.monitoring.jdbc
   [clojure.java.jdbc :as jdbc]
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]))

(deftest test-mem-h2
  (let [db (h2/new-h2-database h2/DEFAULT-MEM-SPEC)
        db (component/start db)]
    (jdbc/execute! db ["CREATE TABLE Temp (id int, name varchar);"])
    (jdbc/insert! db "Temp" {:id 1 :name "Bob"})
    (is (= "Bob" (:name (first (jdbc/query db ["SELECT * FROM Temp;"])))))
    (component/stop db)))

(deftest mem-h2-monitoring-status
  (is (= (-> (h2/new-h2-database h2/DEFAULT-MEM-SPEC)
             component/start
             monitoring/status)
         :running))
  (is (= (-> (h2/new-h2-database h2/DEFAULT-MEM-SPEC)
             component/start
             component/stop
             monitoring/status)
         :down)))
