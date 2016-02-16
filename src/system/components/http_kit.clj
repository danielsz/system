(ns system.components.http-kit
  (:require [system.util :as util]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
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

(def positive-int (s/both s/Int (s/pred pos? 'pos?)))

(def Options
  {(s/optional-key :ip) s/Str
   (s/optional-key :port) positive-int
   (s/optional-key :thread) positive-int
   (s/optional-key :worker-name-prefix) s/Str
   (s/optional-key :queue-size) positive-int
   (s/optional-key :max-body) positive-int
   (s/optional-key :max-line) positive-int})

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
