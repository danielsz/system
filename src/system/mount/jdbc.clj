(ns system.mount.jdbc
  (:require [system.mount :refer [defstate config]]
            [clojure.java.jdbc :as jdbc]))

(defstate database
  :start (jdbc/get-connection (get-in config [:jdbc :spec]))
  :stop (.close database))
