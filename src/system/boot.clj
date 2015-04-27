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

(core/deftask run
  "Run the -main function in some namespace with arguments."
  [m main-namespace NAMESPACE str   "The namespace containing a -main function to invoke."
   a arguments      EXPR      [edn] "An optional argument sequence to apply to the -main function."]
  (core/with-pre-wrap fs
    (require (symbol main-namespace) :reload)
    (if-let [f (resolve (symbol main-namespace "-main"))]
      (apply f arguments)
      (throw (ex-info "No -main method found" {:main-namespace main-namespace})))
    fs))
