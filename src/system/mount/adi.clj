(ns system.mount.adi
  (:require [system.mount :refer [defstate config]]
            [adi.core :as adi]))

(defstate adi-db
  :start (adi/connect! (:adi config))
  :stop (adi/disconnect! adi-db))
