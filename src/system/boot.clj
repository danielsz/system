(ns system.boot
  {:boot/export-tasks true}
  (:require
   [system.repl :refer [init start reset refresh]]
   [clojure.tools.namespace.dir :as dir]
   [clojure.tools.namespace.track :as track]
   [boot.core       :as core]
   [boot.util       :as util]
   [clojure.string :as str]))

(defn- modified-files [before-fileset after-fileset]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files))

(defn- restart? [before-fileset after-fileset files]
  (when files (->> (modified-files before-fileset after-fileset)
                   (core/by-name files)
                   not-empty
                   boolean)))

(core/deftask system [s sys SYS code "The system var."
                      a auto bool "Manages the lifecycle of the application automatically."
                      f files FILES [str] "A vector of files. Will reset the system if a filename in the supplied vector changes."]
  (#'clojure.core/load-data-readers)
  (alter-var-root #'clojure.main/repl-requires conj '[system.repl :refer [init start reset]])
  (let [fs-prev-state (atom nil)
        dirs (into [] (core/get-env :directories))
        tracker (atom (dir/scan-dirs (track/tracker) dirs))
        init-system (delay (do (init sys) (util/info (str sys " " (start) "\n"))))]
    (fn [next-task]
      (fn [fileset]
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)}
          (when auto
            (when (realized? init-system)
              (swap! tracker dir/scan-dirs)
              (util/info (str sys ":refreshing\n"))
              (refresh tracker)
              (when (restart? fs-prev-state fileset files)
                (util/info (str sys ":restarting\n"))
                (reset)))
            @init-system)
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

