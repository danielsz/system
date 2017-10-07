(ns system.components.lambdacd
  (:require [com.stuartsierra.component :as component]
            [lambdacd.core :as lambdacd]
            [lambdacd.runners :as runners]))

(defn start-pipelines [pipelines]
  (doseq [pipeline pipelines]
    (runners/start-one-run-after-another pipeline)))

(defn stop-pipelines [pipelines]
  (doseq [pipeline pipelines]
    (when-let [old-ctx (:context pipeline)]
      ((get-in old-ctx [:config :shutdown-sequence]) old-ctx))))

(defn assemble-pipelines [pipeline-defs config]
  (map
   (fn [[pname pdef]]
     (assoc (lambdacd/assemble-pipeline pdef config)
            :name pname))
   pipeline-defs))

(defrecord Pipelines [pipeline-defs config]
  component/Lifecycle
  (start [component]
    (let [pipelines (assemble-pipelines pipeline-defs config)]
      (start-pipelines pipelines)
      (assoc component :pipelines pipelines)))
  (stop [component]
    (if-let [pipelines (:pipelines component)]
      (do (stop-pipelines pipelines)
          (dissoc component :pipelines))
      component)))

(defn new-lambdacd-pipeline [pipeline-defs config]
  (map->Pipelines {:pipeline-defs pipeline-defs
                   :config config}))
