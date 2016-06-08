(ns system.mount.postgres
  (:require [clojure.java.jdbc :as jdbc]
            [system.mount :refer [defstate config]]))

(defstate postgres-database
  :start (jdbc/get-connection (get-in config [:postgres :spec]))
  :stop (.close postgres-database))
