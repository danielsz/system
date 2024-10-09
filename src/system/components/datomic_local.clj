(ns system.components.datomic-local
  (:require [com.stuartsierra.component :as component]
            [datomic.client.api :as d]))

(defrecord DatomicLocal [cfg db init-fn]
  component/Lifecycle
  (start [component]
    (let [client (d/client cfg)
          _ (d/create-database client db)
          conn (d/connect client db)]
      (when init-fn
        (init-fn conn))
      (assoc component :client client :conn conn)))
  (stop [component]
    (dissoc component :client :conn)))

(defn new-datomic-local [& {:keys [cfg db init-fn]}]
  (map->DatomicLocal {:cfg cfg :db db :init-fn init-fn}))
