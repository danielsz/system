(ns system.components.neo4j-test
  (:use clojure.test)
  (:require [system.components.neo4j :as neo4j]
            [clojurewerkz.neocons.rest.nodes :as nodes :only [create get delete]]
            [com.stuartsierra.component :as component]))

(def uri "http://localhost:7474/db/data/")

(deftest test-neo4j
  (let [db   (neo4j/new-neo4j-db uri)
        db   (component/start db)
        data {:foo "bar"}]
    (is (:conn db) "conn has been added to component")

    (let [node (nodes/create (:conn db) data)]
      (is data (:data (nodes/get db (:id node))))
      (nodes/delete db (:id node)))

    (component/stop db)
    (is (nil? (:conn db)) "conn has been removed from component")))
