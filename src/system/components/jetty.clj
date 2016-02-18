(ns system.components.jetty
  (:require [schema.core :as s]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (get-in component [:handler :handler] handler)
          server (run-jetty handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (.stop server)
      component)))

(def pos-int (s/both s/Int (s/pred pos?)))
(def any-port (s/both s/Int (s/pred #(<= 0 % 65535))))

(def Options
  {(s/optional-key :configurator) s/Any
   (s/optional-key :port) any-port
   (s/optional-key :host) s/Str
   (s/optional-key :join?) s/Bool
   (s/optional-key :daemon?) s/Bool
   (s/optional-key :ssl?) s/Bool
   (s/optional-key :ssl-port) any-port
   (s/optional-key :keystore) s/Str
   (s/optional-key :key-password) s/Str
   (s/optional-key :truststore) s/Str
   (s/optional-key :trust-password) s/Str
   (s/optional-key :max-threads) pos-int 
   (s/optional-key :min-threads) pos-int
   (s/optional-key :max-idle-time) pos-int
   (s/optional-key :client-auth) s/Any
   (s/optional-key :send-date-header?) s/Bool
   (s/optional-key :output-buffer-size) pos-int
   (s/optional-key :request-header-size) pos-int
   (s/optional-key :response-header-size) pos-int})

(defn new-web-server
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options (merge {:port port :join? false}
                                                        options))
                    :handler handler})))


