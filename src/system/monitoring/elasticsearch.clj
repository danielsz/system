(ns system.monitoring.elasticsearch
  (:require system.components.elasticsearch
            [system.monitoring.monitoring :as m])
  (:import [system.components.elasticsearch Elasticsearch]))

(extend-type Elasticsearch
  m/Monitoring
  (status [component]
    (if (:client component) :running :down)))
