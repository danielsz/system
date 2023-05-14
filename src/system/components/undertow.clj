(ns system.components.undertow
  (:require [ring.adapter.undertow :refer [run-undertow]]
            [com.stuartsierra.component :as component]
            [lang-utils.core :refer [seek]]))

(defrecord WebServer [handler options]
  component/Lifecycle
  (start [component]
    (let [handler (if (fn? handler) handler (:handler (val (seek (comp :handler val) component))))
          server (run-undertow handler options)]
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (.stop server))
    (assoc component :server nil)))

(defn new-undertow [& {:keys [port handler options]}]
  (map->WebServer {:options (merge {:host "0.0.0.0" :port port} options)
                   :handler handler}))
