(ns system.components.immutant-web
  (:require [schema.core :as s]
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
  {(s/optional-key :host) s/Str
   (s/optional-key :port) (s/both s/Int (s/pred pos?))
   (s/optional-key :path) s/Str
   (s/optional-key :virtual-host) s/Str
   (s/optional-key :dispatch?) s/Bool
   (s/optional-key :servlet-name) s/Str})

(defn new-web-server
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options (merge {:host "0.0.0.0" :port port}
                                               options))
                    :handler handler})))
