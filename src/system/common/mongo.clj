(ns system.common.mongo
  (:require [monger.core :as mg]
            [monger.credentials :as mcred])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn connect [{:keys [uri dbname server-address server-port opts user password]}]
  (cond
    opts  (let [^MongoOptions opts (mg/mongo-options opts)
                ^ServerAddress sa  (mg/server-address server-address server-port)]
            (if user
              (mg/connect [sa] opts (mcred/create user dbname password))
              (mg/connect sa opts)))
    uri   (:conn (mg/connect-via-uri uri))
    :else (mg/connect)))
