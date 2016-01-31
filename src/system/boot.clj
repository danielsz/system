(ns system.boot
  {:boot/export-tasks true}
  (:require [clojure.tools.namespace.repl :as repl]
            [reloaded.repl :refer [set-init! reset go]]
            [boot.core       :as core]
            [boot.util       :as util]))

(defn- modified-files? [before-fileset after-fileset files]
  (when files (->> (core/fileset-diff @before-fileset after-fileset)
                   core/input-files
                   (core/by-name files)
                   not-empty)))

(core/deftask system [s sys SYS code "The system var."
                      a auto-start bool "Auto-starts the system."
                      f files FILES [str] "Will reset the system if a filename in the supplied vector changes."]
  (set-init! sys)
  (util/info (str "System: " sys "\n"))
  (->> (core/get-env :directories)
       (apply repl/set-refresh-dirs))
  (#'clojure.core/load-data-readers)
  (let [fs-prev-state (atom nil)
        init-system (delay
                     (when auto-start
                       (util/info (str "Autostarting the system: " (go) "\n"))))]
    (fn [next-task]
      (fn [fileset]
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)
                        #'*ns* *ns*} ;because of exception "Can't set!: *ns* from non-binding thread"
          @init-system
          (if (modified-files? fs-prev-state fileset files)
            (reset)
            (repl/refresh))
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
