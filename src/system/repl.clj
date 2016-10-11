(ns system.repl
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.track :as track]
            [system.reload :as reload]
            [clojure.stacktrace :as st]
            [io.aviso.ansi :refer [bold-red bold-yellow]] ))


(declare system)
(declare system-sym)

(defn set-init! [sys]
  (intern 'system.repl 'system-sym (symbol (str (:ns (meta sys))) (str (:name (meta sys))))))

(defn init
  "Constructs the current development system."
  []  
  (alter-var-root #'system (constantly ((find-var system-sym)))))

(defn start
  "Starts the current development system."
  []
  (init)
  (alter-var-root #'system component/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn reset []
  (stop)
  (start))

(defn refresh [tracker {:keys [restart? mode]}]
  (when restart?
    (stop)
    (println (bold-yellow (str "Stopping " system-sym))))

  (when (= mode :tools.namespace) (println "Unmapping namespaces:" (::track/unload @tracker)))
  (println "Recompiling namespaces:" (::track/load @tracker))
  (swap! tracker reload/track-reload (= mode :tools.namespace))
  (when (::reload/error @tracker)
    (swap! boot.core/*warnings* inc)
    (println (bold-red (str "Error reloading: " (::reload/error-ns @tracker))))
    (st/print-throwable (::reload/error @tracker)))

  (when restart?
    (start)
    (println (bold-yellow (str "Starting " system-sym)))))


;; No need to break API signatures

(def go start)

