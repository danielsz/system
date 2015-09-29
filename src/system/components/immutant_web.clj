(ns system.components.immutant-web
  (:require [com.stuartsierra.component :as component]
            [immutant.web :refer [run stop]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (let [handler (condp #(%1 %2) handler
                    fn? handler
                    var? handler
                    (:app handler))
          server (run handler {:port port})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (stop server)
      component)))

(defn new-web-server
  ([port]
   (map->WebServer {:port port}))
  ([port handler]
   (map->WebServer {:port port :handler handler})))
