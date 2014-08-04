(ns system.components.jdbc
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

;; component for a generic JDBC database

(defrecord JDBCDatabase [db-spec connection]
  component/Lifecycle

  (start [component]
    (let [conn (jdbc/get-connection (:db-spec component))]
      (assoc component :connection conn)))

  (stop [component]
    (.close connection)
    (assoc component :connection nil)))

(defn new-database 
  ([db-spec]
    (JDBCDatabase. db-spec nil)))