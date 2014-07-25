(ns framework.components.http-kit
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]))

(defrecord WebServer [port debug server handler]
  component/Lifecycle
  (start [component]
    (let [server (run-server handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (server)
      component)))

(defn new-web-server
  [port debug]
  (map->WebServer {:port port :debug debug :handler handler}))
