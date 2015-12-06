(ns system.boot
  {:boot/export-tasks true}
  (:require [ns-tracker.core :refer :all]
            [reloaded.repl :refer [set-init! reset go]]
            [boot.core       :as core]
            [boot.util       :as util]))


(defn- modified-files? [before-fileset after-fileset files]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files
       (core/by-name files)
       not-empty))

(core/deftask system [s sys SYS code "The system var."
                      a auto-start bool "Auto-starts the system."
                      r hot-reload bool "Enables hot-reloading."
                      f files FILES [str] "A vector of filenames. Restricts hot-reloading to that set."]
  (let [fs-prev-state (atom nil)
        dirs (core/get-env :directories)
        modified-namespaces (ns-tracker (into [] dirs))
        auto-start (delay
                     (when auto-start
                       (util/info (str "Autostarting the system: " (go) "\n"))))]
    (fn [next-task]
      (fn [fileset]
        (#'clojure.core/load-data-readers)
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)}
          (set-init! sys)
          (util/info (str "Current system: " sys "\n"))
          @auto-start
          (when-let [modified (modified-namespaces)]
            (doseq [ns-sym modified]
              (require ns-sym :reload))
            (util/info (str "Reloading namespaces " (pr-str modified) "\n"))
            (when hot-reload (with-bindings {#'*ns* *ns*} ; because of exception "Can't set!: *ns* from non-binding thread"
                               (if files
                                 (when (modified-files? fs-prev-state fileset files) (reset))
                                 (reset)))))
          (next-task (reset! fs-prev-state fileset)))))))

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
