(ns system.components.maxminddb
  (:require [com.stuartsierra.component :as component])
  (:import com.maxmind.db.Reader
           com.maxmind.db.CHMCache))

(defrecord MaxMindDB [db]
  component/Lifecycle
  (start [component]
    (let [client (delay (Reader. db (CHMCache.)))]
      (assoc component :client client)))
  (stop [component]
    (.close @(:client component))
    (dissoc component :client :conn)))

(defn new-maxmind-db [& {:keys [db]}]
  (map->MaxMindDB {:db db}))
