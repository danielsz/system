(ns system.mount.mongo
  (:require [system.common.mongo :as common]
            [system.mount :refer [defstate config]]
            [monger.core :as mg]))

(defstate mongo-conn
  :start (common/connect (:mongo config))
  :stop (try (mg/disconnect mongo-conn)
             (catch Throwable t (println t "Error when stopping Mongo component"))))

(defstate mongo-db
  :start (mg/get-db mongo-conn))
