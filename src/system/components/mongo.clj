(ns system.components.mongo
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]
            [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

(def Options
  {(s/optional-key :connections-per-host) s/Int
   (s/optional-key :threads-allowed-to-block-for-connection-multiplier) s/Int
   (s/optional-key :max-wait-time) s/Int
   (s/optional-key :connect-timeout) s/Int
   (s/optional-key :socket-timeout) s/Int
   (s/optional-key :socket-keep-alive) s/Bool
   (s/optional-key :auto-connect-retry) s/Bool
   (s/optional-key :max-auto-connect-retry-time) s/Int})

(defrecord Mongo [uri init-fn dbname server-address server-port opts]
  component/Lifecycle
  (start [component]
    (cond
      opts (let [^MongoOptions opts (mg/mongo-options opts)
                 ^ServerAddress sa  (mg/server-address server-address server-port)
                 conn               (mg/connect sa opts)
                 db                 (mg/get-db conn dbname)
                 _ (when init-fn (init-fn db))]
             (assoc component :db db))
      uri (let [{:keys [conn db]} (mg/connect-via-uri uri)
                _ (when init-fn (init-fn db))]
            (assoc component :db db))
      :else (let [conn (mg/connect)
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
   (map->Mongo {:uri uri :init-fn init-fn}))
  ([server-address server-port dbname opts]
   (map->Mongo {:server-address server-address :server-port server-port :dbname dbname :opts (s/validate Options opts)}))
  ([server-address server-port dbname opts init-fn]
   (map->Mongo {:server-address server-address :server-port server-port :dbname dbname :opts (s/validate Options opts) :init-fn init-fn})))
