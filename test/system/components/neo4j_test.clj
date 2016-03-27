(ns system.components.neo4j-test
  (:use clojure.test)
  (:require [system.components.neo4j :as neo4j]
            [clojurewerkz.neocons.rest.nodes :as nodes :only [create get delete]]
            [com.stuartsierra.component :as component]))

(def uri "http://localhost:7474/db/data/")
(def data {:foo "bar"})

(deftest ^:dependency test-neo4j
  (let [{:keys [conn] :as db} (-> (neo4j/new-neo4j-db uri)
                                  component/start)]
    (is conn "conn has been added to component")

    (let [node (nodes/create conn data)]
      (is data (:data (nodes/get conn (:id node))))
      (nodes/delete conn (:id node)))

    (let [db (component/stop db)]
      (is (nil? (:conn db))  "conn has been removed from component"))))
