(ns system.components.immutant-web
  (:require [schema.core :as s]
            [system.schema :as sc]
            [com.stuartsierra.component :as component]
            [immutant.web :refer [run stop]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (get-in component [:handler :handler] handler)
          server (run handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (stop server)
      component)))

(def Options
  {(s/optional-key :host) sc/Hostname
   (s/optional-key :port) sc/Port
   (s/optional-key :path) s/Str
   (s/optional-key :virtual-host) s/Str
   (s/optional-key :dispatch?) s/Bool
   (s/optional-key :servlet-name) s/Str})

(s/defn ^:always-validate new-web-server :- WebServer
  ([port :- sc/Port]
   (new-web-server port nil {}))
  ([port :- sc/Port
    handler]
   (new-web-server port handler {}))
  ([port :- sc/Port
    handler
    options :- Options]
   (map->WebServer {:options (merge {:host "0.0.0.0" :port port}
                                    options)
                    :handler handler})))
