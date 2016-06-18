(ns system.boot
  {:boot/export-tasks true}
  (:require
   [system.repl :refer [set-init! start refresh]]
   [clojure.tools.namespace.dir :as dir]
   [clojure.tools.namespace.track :as track]
   [boot.core       :as core]
   [boot.util       :as util]
   [clojure.string :as str]))

(defn- modified-files [before-fileset after-fileset]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files))

(defn- restart? [before-fileset after-fileset files {:keys [paths regexes]}]
  (let [file-filter (cond paths   core/by-path
                          regexes core/by-re
                          :else   core/by-name)
        files (if regexes (map re-pattern files) files)]
    (let [x (when files (->> (modified-files before-fileset after-fileset)
                             (core/by-name files)
                             not-empty
                             boolean))]
      (pr "restart? " x)
      x)))

(core/deftask system
  "Runtime code loading. Automatic System restarts. Fileset driven. 

   Mark your namespace with metadata if you don't want to unload it before reloading its definitions. 
   Valid keys are :passover :blood-of-spring-lamb, :red-pill, :blue-pil or :no-remove-ns.

     You take the blue pill—the story ends. You take the red pill, and I show you how deep the rabbit hole goes."
  [s sys SYS edn "The system Var."
   a auto bool "Manages the lifecycle of the application automatically."
   f files FILES [str] "A vector of files. Will reset the system if a filename in the supplied vector changes."
   r regexes bool "Treat --files as regexes, not file names. Only one of regexes|paths is allowed."
   p paths   bool "Treat --files as classpath paths, not file names. Only one of regexes|paths is allowed."]
  (#'clojure.core/load-data-readers)
  (alter-var-root #'clojure.main/repl-requires conj '[system.repl :refer [set-init! start go stop reset]])
  (let [fs-prev-state (atom nil)
        dirs (into [] (core/get-env :directories))
        tracker (atom (dir/scan-dirs (track/tracker) dirs))
        init-system (delay (do (set-init! sys)
                               (start)
                               (util/info (str "Starting " sys "\n"))))]
    (when (and regexes paths)
      (util/fail "You can only specify --regexes or --paths, not both.\n")
      (*usage*))
    (fn [next-task]
      (fn [fileset]
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)}
          (when auto
            (when (realized? init-system)
              (swap! tracker dir/scan-dirs)
              (util/info (str sys ":refreshing\n"))
              (refresh tracker {:restart? (restart? fs-prev-state fileset files {:regexes regexes :paths paths})}))
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

