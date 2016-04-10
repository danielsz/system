(ns system.components.hikari-test
    (:use clojure.test)
    (:require [system.components.hikari :as h]
              [clojure.java.jdbc :as jdbc]
              [com.stuartsierra.component :as component]))

(deftest hikari-test
  (let [db (h/new-hikari-cp h/DEFAULT-H2-MEM-SPEC)
        db (component/start db)]
    (is (= (type (:datasource db))
           com.zaxxer.hikari.HikariDataSource))
    (jdbc/execute! db ["CREATE TABLE Temp (id int, name varchar);"])
    (jdbc/insert! db "Temp" {:id 1 :name "Bob"})
    (is (= "Bob" (:name (first (jdbc/query db ["SELECT * FROM Temp;"])))))
    (is (nil? (:datasource (component/stop db))))))
