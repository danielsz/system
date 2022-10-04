(ns system.components.jetty
  (:require [schema.core :as s]
            [system.schema :as sc]
            [com.stuartsierra.component :as component]
            [lang-utils.core :refer [seek]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [options handler]
  component/Lifecycle
  (start [component]
    (if (:server component)
      component
      (let [handler (if (fn? handler) handler (:handler (val (seek (comp :handler val) component))))
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

(defn new-web-server
  "Deprecated - this function will eventually be removed. Use keyword arguments instead"
  {:deprecated "0.4.1-SNAPSHOT"}
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options (merge {:port port :join? false}
                                                        options))
                    :handler handler})))

(defn new-jetty [& {:keys [port handler options]}]
  (map->WebServer {:options (s/validate Options (merge {:port port :join? false} options))
                   :handler handler}))
