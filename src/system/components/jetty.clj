(ns system.components.jetty
  (:require [schema.core :as s]
            [system.schema :as sc]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [options handler]
  component/Lifecycle
  (start [component]
    (if (:server component)
      component
      (let [handler (get-in component [:handler :handler] handler)
            server (run-jetty handler options)]
        (assoc component :server server))))
  (stop [component]
    (if-let [server (:server component)]
      (do (.stop server)
          (.join server)
          (dissoc component :server))
      component)))

(def Options
  {(s/optional-key :configurator) s/Any
   (s/optional-key :port) sc/Port
   (s/optional-key :host) sc/Hostname
   (s/optional-key :join?) s/Bool
   (s/optional-key :daemon?) s/Bool
   (s/optional-key :ssl?) s/Bool
   (s/optional-key :ssl-port) sc/Port
   (s/optional-key :keystore) s/Str
   (s/optional-key :key-password) s/Str
   (s/optional-key :truststore) s/Str
   (s/optional-key :trust-password) s/Str
   (s/optional-key :max-threads) sc/PosInt 
   (s/optional-key :min-threads) sc/PosInt
   (s/optional-key :max-idle-time) sc/PosInt
   (s/optional-key :client-auth) s/Any
   (s/optional-key :send-date-header?) s/Bool
   (s/optional-key :output-buffer-size) sc/PosInt
   (s/optional-key :request-header-size) sc/PosInt
   (s/optional-key :response-header-size) sc/PosInt})

(s/defn ^:always-validate new-web-server :- WebServer
  ([port :- sc/Port]
   (new-web-server port nil {}))
  ([port :- sc/Port
    handler]
   (new-web-server port handler {}))
  ([port :- sc/Port
    handler
    options :- Options]
   (map->WebServer {:options (merge {:port port :join? false}
                                    options)
                    :handler handler})))
