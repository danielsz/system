(ns system.mount.etsy
  (:require [system.mount :refer [defstate config]]
            [etsy.core :refer [make-client]]))

(defstate etsy-client
  :start (make-client (:etsy config)))
