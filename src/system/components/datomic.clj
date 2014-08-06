(ns system.components.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defrecord Datomic [uri db]
  component/Lifecycle
  (start [component]
    (let [db (d/create-database uri)
          conn (d/connect uri)]
      (assoc component :db (d/db conn))))
  (stop [component]
    (assoc component :db nil)))

(defn new-datomic-db [uri]
  (map->Datomic {:uri uri}))

