(ns system.components.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defrecord Datomic [uri conn]
  component/Lifecycle
  (start [component]
    (let [db (d/create-database uri)
          conn (d/connect uri)]
      (assoc component :conn conn)))
  (stop [component]
    (when conn (d/release conn))
    (assoc component :conn nil)))

(defn new-datomic-db [uri]
  (map->Datomic {:uri uri}))
