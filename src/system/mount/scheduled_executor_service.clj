(ns system.mount.scheduled-executor-service
  (:require [system.mount :refer [defstate config]])
  (:import [java.util.concurrent ScheduledThreadPoolExecutor]))

(defstate scheduler
  :start (ScheduledThreadPoolExecutor. (get-in config [:scheduled-executor :n-threads]))
  :stop (.shutdown scheduler))
