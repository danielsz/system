(ns system.components.elasticsearch
  (:require [com.stuartsierra.component :as component])
  (:import [org.elasticsearch.client.transport TransportClient]
           [org.elasticsearch.common.transport InetSocketTransportAddress]
           [org.elasticsearch.common.settings ImmutableSettings]))

(defrecord Elasticsearch [addresses settings client]
  component/Lifecycle
  (start [component]
    (let [builder (.. (ImmutableSettings/settingsBuilder)
                      (put ^java.util.Map settings))
          client (doto (TransportClient. builder)
                   (.addTransportAddresses (into-array addresses)))]
      (assoc component :client client)))
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
