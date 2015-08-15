(ns system.components.http-kit
  (:require [system.components.app]
            [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]])
  (:import [system.components.app App]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (println handler)
    (println (satisfies? component/Lifecycle handler))
    (let [handler (if (instance? App handler)
                    (:app handler)
                    handler)
          server (run-server handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (server)
      component)))

(def allowed-opts
  [:ip :port :thread :worker-name-prefix :queue-size :max-body :max-line])

(defn assert-only-contains-options! [options]
  (let [invalid-keys (keys (apply dissoc options allowed-opts))]
    (assert (not invalid-keys)
            (format "Invalid option(s) for http-kit: %s"
                    (pr-str invalid-keys)))))

(defn new-web-server
  ([port] (map->WebServer {:options {:port port}}))
  ([port handler] (new-web-server port handler {}))
  ([port handler options]
   (assert-only-contains-options! options)
   (map->WebServer {:options (-> {:port port}
                              (merge options)
                              (select-keys allowed-opts))
                    :handler handler})))
