(ns system.components.repl-server
  (:require [com.stuartsierra.component :as component]))


(defrecord ReplServer [port bind with-cider]
  component/Lifecycle
  (start [component]
    (let [start-server (or (resolve 'nrepl.server/start-server)
                           (resolve 'clojure.tools.nrepl.server/start-server))
          nrepl-handler #(do (require 'cider.nrepl)
                             (ns-resolve 'cider.nrepl 'cider-nrepl-handler))
          handler (when with-cider (nrepl-handler))]
      (assoc component :server (start-server :port port :bind bind :handler handler))))
  (stop [{server :server :as component}]
    (when server
      (let [stop-server (or (resolve 'nrepl.server/stop-server)
                            (resolve 'clojure.tools.nrepl.server/stop-server))]
        (stop-server server)
        component))))

(defn new-repl-server
  [& {:keys [port bind with-cider] :or {bind "localhost" with-cider false}}]
  (try
    (require 'nrepl.server)
    (catch java.io.FileNotFoundException e
      (require 'clojure.tools.nrepl.server)))
  (map->ReplServer {:port port :bind bind :with-cider with-cider}))
