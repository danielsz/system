(ns system.components.hikari
  (:require [com.stuartsierra.component :as component]
            [hikari-cp.core :as hikari]
            [clojure.java.jdbc :as jdbc]))

(def DEFAULT-H2-MEM-SPEC
  {:adapter "h2"
   :url     "jdbc:h2:mem:"
   :user     "sa"
   :password ""})

; db-spec format: https://github.com/tomekw/hikari-cp#configuration-options
(defrecord Hikari [db-spec datasource]
  component/Lifecycle
  (start [component]
    (let [s (or datasource (hikari/make-datasource db-spec))]
      (assoc component :datasource s)))
  (stop [component]
    (when datasource
      (hikari/close-datasource datasource))
    (assoc component :datasource nil)))

(defn new-hikari-cp [db-spec]
  (map->Hikari {:db-spec db-spec}))
