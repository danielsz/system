(ns system.components.immutant-web
  (:require [com.stuartsierra.component :as component]
            [immutant.web :refer [run stop]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (let [server (run handler {:port port})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (stop server)
      component)))

(defn new-web-server
  [port handler]
  (map->WebServer {:port port :handler handler}))
