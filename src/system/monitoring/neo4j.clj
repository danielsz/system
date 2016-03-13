(ns system.monitoring.neo4j
  (:require system.components.neo4j
            [system.monitoring.monitoring :as m])
  (:import [system.components.neo4j Neo4j]))

(extend-type Neo4j
  m/Monitoring
  (status [component]
    (if (:conn component) :running :down)))
