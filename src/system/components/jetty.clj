(ns system.components.jetty
  (:require [system.components.app]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (let [handler (if (or (fn? handler) (= (type handler) clojure.lang.Var))
                    handler
                    (:app handler))
          server (run-jetty handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (.stop server)
      component)))

(defn new-web-server
  ([port]
   (map->WebServer {:port port}))
  ([port handler]
   (map->WebServer {:port port :handler handler})))
