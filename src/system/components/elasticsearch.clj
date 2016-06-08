(ns system.components.elasticsearch
  (:require [com.stuartsierra.component :as component]
            [system.common.elasticsearch :as common])
  (:import [org.elasticsearch.client.transport TransportClient]
           [org.elasticsearch.common.transport InetSocketTransportAddress]))

(defrecord Elasticsearch [addresses settings client]
  component/Lifecycle
  (start [component]
    (assoc component :client (common/mk-client addresses settings)))
  (stop [component]
    (when client
      (.close ^TransportClient client))
    (assoc component :client nil)))

(defn new-elasticsearch-db
  ([addresses]
    (new-elasticsearch-db addresses {}))
  ([addresses settings]
    (map->Elasticsearch {:addresses (for [[^String host ^int port] addresses]
                                      (InetSocketTransportAddress. host port))
                         :settings settings})))
