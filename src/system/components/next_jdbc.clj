(ns system.components.next-jdbc
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]))

(defrecord NextJDBCDatabase [db-spec jdbc-url]
  component/Lifecycle
  (start [component]
    (let [datasource (if jdbc-url
                       (jdbc/get-datasource jdbc-url)
                       (jdbc/get-datasource db-spec))]
      (assoc component :datasource datasource)))
  (stop [component]
    (assoc component :datasource nil)))

(defn new-next-jdbc  [& {:keys [db-spec jdbc-url]}]
  (map->NextJDBCDatabase {:db-spec db-spec :jdbc-url jdbc-url}))
