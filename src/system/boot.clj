(ns system.boot
  {:boot/export-tasks true}
  (:require [ns-tracker.core :refer :all]
            [reloaded.repl :refer [set-init! reset]]
            [boot.core       :as core]
            [boot.util       :as util]))

(def dirs (core/get-env :directories))

(def modified-namespaces
  (ns-tracker (into [] dirs)))

(core/deftask system [s sys SYS code "The system to restart in the REPL workflow"] 
  (core/with-pre-wrap fileset
    (set-init! sys)
    (util/info (str "Current system: " sys "\n"))
    (when-let [modified (modified-namespaces)]
      (doseq [ns-sym modified]
        (require ns-sym :reload))
      (util/info (str "Reloading " (pr-str modified) "\n")))
    fileset))
