(ns system.mount.datomic
  (:require [com.stuartsierra.component :as component]
            [system.mount :refer [defstate config]]
            [datomic.api :as d]))

(defstate datomic-db
  :start (let [uri (get-in config [:datomic :uri])]
           (d/create-database uri)
           (d/connect uri))
  :stop (d/release datomic-db))
