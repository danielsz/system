(ns system.monitoring.jdbc
  (:require system.components.jdbc
            [system.monitoring.monitoring :as m])
  (:import [system.components.jdbc JDBCDatabase]))

(extend-protocol m/Monitoring
  JDBCDatabase
  (status [component]
    (if (:connection component) :running :down))
  clojure.lang.PersistentArrayMap
  (status [component]
    (if (:connection component) :running :down)))
