(ns system.components.postgres-test
  (:use clojure.test)
  (:require [system.components.postgres :as p]
            [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component]))


;; Assumes you have run `script/pg_test_setup.sh'

(def test-db-spec
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "system_test_db"
   :user    "system_test_user"
   :password ""
   :host "127.0.0.1"})

(deftest postgres-test-create-table-and-insert
  (let [db (component/start
            (p/new-postgres-database test-db-spec))
        msg "It works!"]
    (jdbc/execute! db ["CREATE TEMP TABLE test (coltest varchar(20));"])
    (jdbc/insert! db :test {:coltest msg})
    (is (= msg (:coltest (first (jdbc/query db ["SELECT * from test;"])))))
    (component/stop db)))


