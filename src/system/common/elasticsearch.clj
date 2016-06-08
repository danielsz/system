(ns system.common.elasticsearch
  (:import [org.elasticsearch.client.transport TransportClient]
           [org.elasticsearch.common.settings ImmutableSettings]))

(defn mk-client [addresses settings]
  (let [builder (.. (ImmutableSettings/settingsBuilder)
                    (put ^java.util.Map settings))]
    (doto (TransportClient. builder)
      (.addTransportAddresses (into-array addresses)))))
