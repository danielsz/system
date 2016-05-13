(ns system.repl
  (require [com.stuartsierra.component :as component]
           [clojure.tools.namespace.track :as track]
           [system.reload :as reload]
           [clojure.stacktrace :as st]
           [io.aviso.ansi :refer [bold-red]] ))


(declare system)
(declare system-sym)

(defn set-init! [sys]
  (if (var? sys)
    (intern 'system.repl 'system-sym (symbol (str (:ns (meta sys))) (str (:name (meta sys)))))
    (throw (Exception. "sys-init! expects a Var, eg. #'system-dev "))))

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

(defn refresh [tracker {:keys [restart?]}]
  (when restart? (stop))

  (println "Unloading:" (::track/unload @tracker))
  (println "Reloading:" (::track/load @tracker))
  (swap! tracker reload/track-reload)
  (when (::reload/error @tracker)
    (println (bold-red (str "Error reloading: " (::reload/error-ns @tracker))))
    (st/print-throwable (::reload/error @tracker)))

  (when restart? (start)))


;; No need to break API signatures

(def go start)

