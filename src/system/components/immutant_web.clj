(ns system.components.immutant-web
  (:require [system.util :as util]
            [com.stuartsierra.component :as component]
            [immutant.web :refer [run stop]]))

(defrecord WebServer [options server handler]
  component/Lifecycle
  (start [component]
    (let [handler (condp #(%1 %2) handler
                    fn? handler
                    var? handler
                    (:app handler))
          server (run handler options)]
      (assoc component :server server)))
  (stop [component]
    (when server
      (stop server)
      component)))

(def allowed-opts
  [:host :port :path :virtual-host :dispatch? :servlet-name])

(defn new-web-server
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (util/assert-only-contains-options! "immutant-web" options allowed-opts)
   (map->WebServer {:options (merge {:host "0.0.0.0" :port port}
                                    options)
                    :handler handler})))
