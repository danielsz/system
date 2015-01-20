(ns system.components.aleph
  (:require [com.stuartsierra.component :as component]
            [aleph.http :refer [start-server]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (let [server (start-server handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (.close server)
      component)))
(defn new-web-server
  [port handler]
  (map->WebServer {:port port :handler handler}))
