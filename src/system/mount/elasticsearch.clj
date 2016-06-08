(ns system.mount.elasticsearch
  (:require [system.common.elasticsearch :as common]
            [system.mount :refer [defstate config]])
  (:import [org.elasticsearch.client.transport TransportClient]))

(defstate elasticsearch-db
  :start (common/mk-client (get-in config [:elasticsearch :addresses])
                           (get-in config [:elasticsearch :settings]))
  :stop (.close ^TransportClient elasticsearch-db))
