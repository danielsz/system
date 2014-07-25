(ns framework.components.mongo
  (:require [com.stuartsierra.component :as component]
            [monger.core :as mg]))

(defrecord Mongo [uri db]
  component/Lifecycle
  (start [component]
    (if uri 
      (let [{:keys [conn db]} (mg/connect-via-uri uri)]
        (assoc component :db db))
      (let [conn (mg/connect)]
        (assoc component :db (mg/get-db conn "mongo-dev")))))
  (stop [component]
    (assoc component :db nil)))

(defn new-mongo-db
  ([]
     (map->Mongo {}))
  ([uri]
     (map->Mongo {:uri uri})))



