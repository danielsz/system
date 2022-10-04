(ns system.components.immutant-web
  (:require [schema.core :as s]
            [system.schema :as sc]
            [com.stuartsierra.component :as component]
            [lang-utils.core :refer [seek]]
            [immutant.web :refer [run stop]]))

(defrecord WebServer [handler options]
  component/Lifecycle
  (start [component]
    (let [handler (if (fn? handler) handler (:handler (val (seek (comp :handler val) component))))
          server (run handler options)]
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (stop server))
    (assoc component :server nil)))

(def Options
  {(s/optional-key :host) sc/Hostname
   (s/optional-key :port) sc/Port
   (s/optional-key :path) s/Str
   (s/optional-key :virtual-host) s/Str
   (s/optional-key :dispatch?) s/Bool
   (s/optional-key :servlet-name) s/Str})

(defn new-web-server
  "Deprecated - this function will eventually be removed. Use keyword arguments instead"
  {:deprecated "0.4.1-SNAPSHOT"}
  ([port]
   (new-web-server port nil {}))
  ([port handler]
   (new-web-server port handler {}))
  ([port handler options]
   (map->WebServer {:options (s/validate Options (merge {:host "0.0.0.0" :port port} options))
                    :handler handler})))

(defn new-immutant-web [& {:keys [port handler options]}]
  (map->WebServer {:options (s/validate Options (merge {:host "0.0.0.0" :port port} options))
                   :handler handler}))
