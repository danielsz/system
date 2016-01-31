(ns system.boot
  {:boot/export-tasks true}
  (:require [clojure.tools.namespace.repl :as repl]
            [reloaded.repl :refer [set-init! go]]
            [boot.core       :as core]
            [boot.util       :as util]))

(core/deftask system [s sys SYS code "The system var."
                      a auto-start bool "Auto-starts the system."]
  (let [dirs (core/get-env :directories)
        _ (apply repl/set-refresh-dirs dirs)]
    (fn [next-task]
      (fn [fileset]
        (#'clojure.core/load-data-readers)
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)}
          (set-init! sys)
          (util/info (str "Current system: " sys "\n"))
          (when auto-start
            (util/info (str "Autostarting the system: " (go) "\n")))
          (with-bindings {#'*ns* *ns*} ; because of exception "Can't set!: *ns* from non-binding thread"
            (repl/refresh))
          (next-task fileset))))))

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
