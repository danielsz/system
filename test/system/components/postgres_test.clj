(ns system.components.postgres-test
  (:use clojure.test)
  (:require [system.components.postgres :as p]
            [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component]))


;; Assumes you have a database named "test" setup and
;; PostgreSQL running. Also, create a user with username "test"
;; and password "test".


(deftest postgres-test-create-table-and-insert
  (let [db (component/start
            (p/new-postgres-database p/DEFAULT-DB-SPEC))
        msg "It works!"]
    (jdbc/execute! db ["CREATE TEMP TABLE test (coltest varchar(20));"])
    (jdbc/insert! db :test {:coltest msg})
    (is (= msg (:coltest (first (jdbc/query db ["SELECT * from test;"])))))
    (component/stop db)))


