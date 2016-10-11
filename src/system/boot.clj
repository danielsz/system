(ns system.boot
  {:boot/export-tasks true}
  (:require
   [system.repl :refer [set-init! start refresh]]
   [clojure.tools.namespace.dir :as dir]
   [clojure.tools.namespace.track :as track]
   [clojure.tools.namespace.find :as ns-find]
   [boot.core       :as core]
   [boot.pod        :as pod]
   [boot.util       :as util]
   [clojure.string :as str]
   [clojure.java.io :as io]))

(defn- modified-files [before-fileset after-fileset]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files))

(defn- restart? [before-fileset after-fileset files {:keys [paths regexes]}]
  (let [file-filter (cond paths   core/by-path
                          regexes core/by-re
                          :else   core/by-name)
        files (if regexes (map re-pattern files) files)]
    (when files (->> (modified-files before-fileset after-fileset)
                     (file-filter files)
                     not-empty
                     boolean))))

(defn validate-sys [sys]
  (let [dirs (core/get-env :directories)
        namespaces (set (mapcat #(ns-find/find-namespaces-in-dir (io/file %)) dirs))]
    (cond
      (not (var? sys)) (throw (Exception. "sys argument expects a Var, eg. #'system-dev"))
      (not (= com.stuartsierra.component.SystemMap (type (try (sys)
                                                              (catch Exception e))))) (throw (Exception. (str sys " is not a SystemMap")))
      (not (contains? namespaces (symbol (str (:ns (meta sys)))))) (throw (Exception. "The System's Var has to be defined in the project's namespaces."))
      :else sys)))

(defn validate [{:keys [auto files regexes paths]} usage]
  (when (and regexes paths)
      (util/fail "You can only specify --regexes or --paths, not both.\n")
      (throw (Exception. "Task configuration failed.")))
  (when (and (not auto) files)
      (util/fail "You have specified manual mode, the files vector should not be present in that case.\n")
      (throw (Exception. "Task configuration failed."))))

(core/deftask system
  "Runtime code loading. Automatic System restarts. Fileset driven. 

   Mark your namespace with metadata if you don't want to unload it before reloading its definitions. 
   Valid keys are :passover :blood-of-spring-lamb, :red-pill, :blue-pil or :no-remove-ns.

     You take the blue pill—the story ends. You take the red pill, and I show you how deep the rabbit hole goes."
  [s sys SYS edn "The system Var."
   a auto bool "Manages the lifecycle of the application automatically."
   f files FILES [str] "A vector of files. Will reset the system if a filename in the supplied vector changes."
   r regexes bool "Treat --files as regexes, not file names. Only one of regexes|paths is allowed."
   p paths   bool "Treat --files as classpath paths, not file names. Only one of regexes|paths is allowed."
   m mode MODE kw "Standard Lisp mode - recompilation only. Tools.namespace mode - load + unload (default)."]
  (validate *opts* *usage*)
  (alter-var-root #'clojure.main/repl-requires conj '[system.repl :refer [start go stop reset]])
  (let [fs-prev-state (atom nil)
        dirs (into [] (core/get-env :directories))
        tracker (atom (dir/scan-dirs (track/tracker) dirs))
        init-system (if sys
                      (delay (do (set-init! (validate-sys sys))
                                 (start)
                                 (util/info (str "Starting " sys "\n"))))
                      (delay (util/info (str "System was not supplied. Will reload code, but not perform restarts.\n"))))]
    (fn [next-task]
      (fn [fileset]
        (when (and auto (realized? init-system))
          (swap! tracker dir/scan-dirs)
          (util/info (str sys ":refreshing\n"))
          (refresh tracker {:restart? (restart? fs-prev-state fileset files {:regexes regexes :paths paths})
                            :mode (or mode :tools.namespace)}))
        @init-system
        (next-task (reset! fs-prev-state fileset))))))

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

