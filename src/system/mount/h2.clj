(ns system.mount.h2
  (:require [clojure.java.jdbc :as jdbc]
            [system.mount :refer [defstate config]]))

(defstate h2-database
  :start (jdbc/get-connection (get-in config [:h2 :spec]))
  :stop (.close h2-database))
