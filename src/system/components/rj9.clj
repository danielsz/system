(ns system.components.rj9
  (:require [ring.adapter.jetty9 :refer [run-jetty]]
            [com.stuartsierra.component :as component]))

(defrecord WebServer [handler options]
  component/Lifecycle
  (start [component]
    (let [handler (if (fn? handler)
                    handler
                    (get-in component [:handler :handler]))
          server (run-jetty handler options)]
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (.stop server))
    (assoc component :server nil)))

(defn new-rj9 [& {:keys [port handler options]}]
  (map->WebServer {:options (merge {:host "0.0.0.0" :port port :join? false} options)
                   :handler handler}))
