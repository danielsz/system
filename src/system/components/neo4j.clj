(ns system.components.neo4j
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.neocons.rest :as n]))

(defrecord Neo4j [uri user passwd conn]
  component/Lifecycle
  (start [component]
    (cond
      (and uri user passwd) (assoc component :conn (n/connect uri user passwd))
      uri                   (assoc component :conn (n/connect uri))))
  (stop [component]
    (assoc component :conn nil)))

(defn new-neo4j-db
  ([uri]
     (map->Neo4j {:uri uri}))
  ([uri user passwd]
     (map->Neo4j {:uri uri :user user :passwd passwd})))
