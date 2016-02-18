(ns system.components.http-kit
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]
            [system.schema :as ss]
            [org.httpkit.server :refer [run-server]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (get-in component [:handler :handler] handler)
          server (run-server handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (server)
      component)))

(def Options
  {(s/optional-key :ip) ss/IpAddress
   (s/optional-key :port) ss/Port
   (s/optional-key :thread) ss/PosInt
   (s/optional-key :worker-name-prefix) s/Str
   (s/optional-key :queue-size) ss/PosInt
   (s/optional-key :max-body) ss/PosInt
   (s/optional-key :max-line) ss/PosInt})

(defn new-web-server
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options 
                                         (merge {:port port}
                                                options))
                    :handler handler})))
