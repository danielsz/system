(ns system.mount.cider-repl-server
  (:require [system.mount :refer [defstate config]]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [cider.nrepl :refer (cider-nrepl-handler)]))

(defstate cider-repl-server
  :start (start-server :port (get-in config [:nrepl-server :port]) :handler cider-nrepl-handler)
  :stop (stop-server cider-repl-server))
