(ns system.repl
  (require [com.stuartsierra.component :as component]
           [clojure.tools.namespace.track :as track]
           [system.reload :as reload]))

(declare system)

(defn init
  "Constructs the current development system."
  [sys]
  (alter-var-root #'system  (constantly (sys))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system component/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn reset []
  (stop)
  (start))

(defn refresh [tracker]
  ;(println :unloading (::track/unload @tracker))
  (println :reloading (::track/load @tracker))
  (when (::reload/error @tracker) (println "Error reloading" (::reload/error-ns @tracker)))
  (swap! tracker reload/track-reload))


;; No need to break API signatures

(def go start)
(def set-init! init)
