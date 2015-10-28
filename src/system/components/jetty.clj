(ns system.components.jetty
  (:require [system.components.app]
            [system.util :as util]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (condp #(%1 %2) handler
                    fn? handler
                    var? handler
                    (:app handler))
          server (run-jetty handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (.stop server)
      component)))

(def allowed-opts
  [:configurator
   :port
   :host
   :join?
   :daemon?
   :ssl?
   :ssl-port
   :keystore
   :key-password
   :truststore
   :trust-password
   :max-threads
   :min-threads
   :max-idle-time
   :client-auth
   :send-date-header?
   :output-buffer-size
   :request-header-size
   :response-header-size])

(defn new-web-server
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (util/assert-options! "jetty" options allowed-opts)
   (map->WebServer {:options (merge {:port port :join? false}
                                    options)
                    :handler handler})))


 


