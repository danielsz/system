(ns system.components.http-kit
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]
            [system.schema :as sc]
            [lang-utils.core :refer [seek]]
            [org.httpkit.server :refer [run-server]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (if (fn? handler) handler (:handler (val (seek (comp :handler val) component))))
          server (run-server handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (server)
      component)))

(def Options
  {(s/optional-key :ip) sc/IpAddress
   (s/optional-key :port) sc/Port
   (s/optional-key :thread) sc/PosInt
   (s/optional-key :worker-name-prefix) s/Str
   (s/optional-key :queue-size) sc/PosInt
   (s/optional-key :max-body) sc/PosInt
   (s/optional-key :max-line) sc/PosInt})

(defn new-web-server
  "Deprecated - this function will eventually be removed. Use keyword arguments instead"
  {:deprecated "0.4.1-SNAPSHOT"}
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options 
                                         (merge {:port port}
                                                options))
                    :handler handler})))

(defn new-http-kit [& {:keys [port handler options]}]
  (map->WebServer {:options (s/validate Options (merge {:port port} options))
                   :handler handler}))
