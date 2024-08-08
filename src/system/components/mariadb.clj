(ns system.components.mariadb
  (:require [com.stuartsierra.component :as component])
  (:import [org.mariadb.jdbc MariaDbDataSource]))

(defrecord MariaDB [jdbc-url init-fn]
  component/Lifecycle
  (start [component]
    (let [data-source (MariaDbDataSource. jdbc-url)
          connection (.getConnection data-source)]
      (when init-fn (init-fn {:connection connection :data-source data-source}))
      (assoc component :connection connection :data-source data-source)))
  (stop [component]
    (when-let [connection (:connection component)]
      (.close connection))
    (assoc component :connection nil :data-source nil)))

(defn new-mariadb  [& {:keys [jdbc-url init-fn]}]
  (map->MariaDB {:jdbc-url jdbc-url :init-fn init-fn}))
