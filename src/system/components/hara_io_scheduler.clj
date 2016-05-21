(ns system.components.hara-io-scheduler
  (:require
    [com.stuartsierra.component :as component]
    [hara.io.scheduler :as sch]))

(defrecord Scheduler [scheduler]
  component/Lifecycle
  (start [component]
    (sch/start! scheduler)
    (assoc component :scheduler scheduler))
  (stop [component]
    (sch/stop! scheduler)
    (dissoc component :scheduler)))

(defn new-scheduler
  ([]
   (new-scheduler (sch/scheduler {})))
  ([scheduler]
   (map->Scheduler {:scheduler scheduler})))