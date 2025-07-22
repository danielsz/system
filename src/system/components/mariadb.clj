(ns system.components.mariadb
  (:require [com.stuartsierra.component :as component])
  (:import [org.mariadb.jdbc MariaDbDataSource MariaDbPoolDataSource]))

(defrecord MariaDB [jdbc-url init-fn with-pool? with-connection?]
  component/Lifecycle
  (start [component]
    (let [data-source (if with-pool? (MariaDbPoolDataSource. jdbc-url)
                          (MariaDbDataSource. jdbc-url))]
      (when init-fn (init-fn {:data-source data-source}))
      (cond-> component
        true (assoc :data-source data-source)
        with-connection? (assoc :connection (.getConnection data-source)))))
  (stop [component]
    (when-let [connection (:connection component)]
      (.close connection))
    (when with-pool? (.close (:data-source component)))
    (dissoc component :data-source)))

(defn new-mariadb  [& {:keys [jdbc-url init-fn with-pool? with-connection?] :or {with-pool? false with-connection? false} :as options}]
  (map->MariaDB options))
