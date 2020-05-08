(ns system.components.mariadb
  (:require [com.stuartsierra.component :as component])
  (:import [java.sql DriverManager]))

(defrecord MariaDBConnection [jdbc-url]
  component/Lifecycle
  (start [component]
    (let [connection (DriverManager/getConnection jdbc-url )]
      (assoc component :connection connection)))
  (stop [component]
    (when-let [connection (:connection component)]
      (.close connection))
    (assoc component :connection nil)))

(defn new-mariadb-connection  [& {:keys [jdbc-url]}]
  (map->MariaDBConnection {:jdbc-url jdbc-url}))
