(ns system.reload
  (:require  [clojure.tools.namespace.track :as track]))

(defn clean-lib
  "Remove lib's mappings (unintern symbols)"
  [lib]
  (when (find-ns lib)
    (doseq [[sym _] (ns-publics lib)]
      (when (ns-resolve lib sym)
        (println "removing" sym "from" lib)
        (ns-unmap lib sym)))
    (doseq [[sym _] (ns-aliases lib)]
      (ns-unalias lib sym))))

(defn remove-lib
  "Remove lib's namespace and remove lib from the set of loaded libs."
  [lib]
  (if-not (some #{:passover :blood-of-spring-lamb :red-pill :blue-pill :skip-remove-ns :פסח} (keys (meta (find-ns lib))))
    (do (remove-ns lib)
        (dosync (alter @#'clojure.core/*loaded-libs* disj lib)))
    (do (doseq [[sym _] (ns-aliases lib)]
          (ns-unalias lib sym))
        (println "Passing over" lib))))

(defn track-reload-one
  "Executes the next pending unload/reload operation in the dependency
  tracker. Returns the updated dependency tracker. If reloading caused
  an error, it is captured as ::error and the namespace which caused
  the error is ::error-ns."
  [tracker]
  (let [{unload ::track/unload, load ::track/load} tracker]
    (cond
      (seq unload) (let [n (first unload)]
                     (remove-lib n)
                     (update-in tracker [::track/unload] rest))
      (seq load) (let [n (first load)]
                   (try
                     (require n :reload)
                     (update-in tracker [::track/load] rest)
                     (catch Throwable t
                       (assoc tracker
                              ::error t ::error-ns n ::track/unload load))))
      :else
        tracker)))

(defn track-reload
  "Executes all pending unload/reload operations on dependency tracker
  until either an error is encountered or there are no more pending
  operations."
  [tracker]
  (loop [tracker (dissoc tracker ::error ::error-ns)]
    (let [{error ::error, unload ::track/unload, load ::track/load} tracker]
      (if (and (not error)
               (or (seq load) (seq unload)))
        (recur (track-reload-one tracker))
        tracker))))
