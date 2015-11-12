(ns system.components.quartzite
  (:require
   [com.stuartsierra.component :as component]
   [clojurewerkz.quartzite.scheduler :as qs]))

(defrecord Scheduler [scheduler]
  component/Lifecycle
  (start [component]
    (let [s (-> (qs/initialize) qs/start)]
      (assoc component :scheduler s)))
  (stop [component]
    (qs/shutdown scheduler)
    component))
  
(defn new-scheduler
  []
  (map->Scheduler {}))
