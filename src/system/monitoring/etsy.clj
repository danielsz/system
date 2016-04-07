(ns system.monitoring.etsy
  (:require system.components.etsy
            [system.monitoring.monitoring :as m])
  (:import [system.components.etsy Etsy]))

(extend-type Etsy
  m/Monitoring
  (status [component]
    (if (:client component) :running :down)))
