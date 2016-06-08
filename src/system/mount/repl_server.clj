(ns system.mount.repl-server
  (:require [system.mount :refer [defstate config]]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]))

(defstate repl-server
  :start (start-server :port (get-in config [:nrepl-server :port]))
  :stop (stop-server repl-server))
