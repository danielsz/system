(ns system.components.jdbc
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

;; component for a generic JDBC database

(defrecord JDBCDatabase [db-spec connection init-fn]
  component/Lifecycle
  (start [component]
    (let [conn (or connection (jdbc/get-connection (:db-spec component)))
          _ (when init-fn (init-fn db-spec))]
      (assoc component :connection conn)))
  (stop [component]
    (when-let [conn (:connection component)]
      (.close conn))
    (dissoc component :connection)))

(defn new-database
  ([db-spec]
   (map->JDBCDatabase {:db-spec db-spec}))
  ([db-spec init-fn]
   (map->JDBCDatabase {:db-spec db-spec :init-fn init-fn})))
