(ns system.boot
  {:boot/export-tasks true}
  (:require [ns-tracker.core :refer :all]
            [reloaded.repl :refer [set-init! reset go]]
            [boot.core       :as core]
            [boot.util       :as util]))


(defn- files-matching-predicate [before-fileset after-fileset predicate]
  (->> (core/fileset-diff @before-fileset after-fileset)
       core/input-files
       predicate
       not-empty))

(defn- modified-files-matching-any-name [before-fileset after-fileset files]
  (files-matching-predicate before-fileset after-fileset (partial core/by-name files)))

(defn- modified-files-matching-any-regex [before-fileset after-fileset regexes]
  (files-matching-predicate before-fileset after-fileset (partial core/by-re regexes)))

(core/deftask system [s sys SYS code "The system var."
                      a auto-start bool "Auto-starts the system."
                      r hot-reload bool "Enables hot-reloading."
                      f files FILES [str] "A vector of filenames. Restricts hot-reloading to that set."
                      x regexes REGEXES #{regex} "A set of regular expressions. Restricts hot-reloading to files matching any regular expression in that set."]
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
            (when hot-reload
              (with-bindings {#'*ns* *ns*} ; because of exception "Can't set!: *ns* from non-binding thread"
                (cond
                  regexes
                    (when (modified-files-matching-any-regex fs-prev-state fileset regexes) (reset))
                  files
                    (when (modified-files-matching-any-name fs-prev-state fileset files) (reset))
                  :else
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
