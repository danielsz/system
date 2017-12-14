(ns system.components.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defrecord Datomic [uri conn init-fn]
  component/Lifecycle
  (start [component]
    (let [db (d/create-database uri)
          conn (d/connect uri)
          _ (when init-fn (init-fn conn))]
      (assoc component :conn conn)))
  (stop [component]
    (when conn (d/release conn))
    (assoc component :conn nil)))

(defn new-datomic-db
  ([uri]
   (map->Datomic {:uri uri}))
  ([uri init-fn]
   (map->Datomic {:uri uri :init-fn init-fn})))
