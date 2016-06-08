(ns system.mount.quartzite
  (:require [system.mount :refer [defstate config]]
            [clojurewerkz.quartzite.scheduler :as qs]))

(defstate scheduler
  :start (-> (qs/initialize) qs/start)
  :stop (qs/shutdown scheduler))
