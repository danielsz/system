(ns system.monitoring.riemann-client
  (:require [system.monitoring.core :as c]
            [riemann.client :as r])
  (:import [system.components.riemann_client RiemannClient]))

(extend-type RiemannClient
  c/Monitoring
  (started? [component]
    (r/connected? (:client component)))
  (stopped? [component]
    (not (r/connected? (:client component)))))
