(ns system.monitoring.adi
  (:require [system.monitoring.monitoring :as m]
            [adi.core :as adi]
            [adi.core.types :refer [map->Adi]]))

(extend-type adi.core.types.Adi
  m/Monitoring
  (status [component]
    (if (:connection component) :running :down)))
