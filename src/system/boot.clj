(ns system.boot
  {:boot/export-tasks true}
  (:require [ns-tracker.core :refer :all]
            [reloaded.repl :refer [set-init! reset]]
            [boot.core       :as core]
            [boot.util       :as util]))

(def dirs (core/get-env :directories))

(def fs-prev-state (atom nil))

(def modified-namespaces
  (ns-tracker (into [] dirs)))

(defn- modified-files? [before-fileset after-fileset files]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files
       (core/by-name files)
       not-empty))

(core/deftask system [s sys SYS code "The system to restart in the boot pipeline"
                      r hot-reload    bool  "Enable hot-reloading."
                      f files    FILES       [str] "A vector of filenames applying to the hot-reloading behavior."]
  (core/with-pre-wrap fileset
    (set-init! sys)
    (util/info (str "Current system: " sys "\n"))
    (when-let [modified (modified-namespaces)]
      (doseq [ns-sym modified]
        (require ns-sym :reload))
      (util/info (str "Reloading namespaces " (pr-str modified) "\n"))
      (when hot-reload (binding [*ns* *ns*]
                         (if files
                           (when (modified-files? fs-prev-state fileset files) (reset))
                           (reset)))))
    (reset! fs-prev-state fileset)))

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
