(ns system.components.mongo
  (:require [com.stuartsierra.component :as component]
            [monger.core :as mg]))

(defrecord Mongo [uri db init-fn]
  component/Lifecycle
  (start [component]
    (if uri 
      (let [{:keys [conn db]} (mg/connect-via-uri uri)
            _ (when init-fn (init-fn db))]
        (assoc component :db db))
      (let [conn (mg/connect)
            db (mg/get-db conn "mongo-dev")]
        (assoc component :db db))))
  (stop [component]
    (assoc component :db nil)))

(defn new-mongo-db
  ([]
     (map->Mongo {}))
  ([uri]
   (map->Mongo {:uri uri}))
  ([uri init-fn]
   (map->Mongo {:uri uri :init-fn init-fn})))



