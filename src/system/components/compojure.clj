(ns system.components.compojure
  (:require
   [com.stuartsierra.component :as component]
   [compojure.core :as compojure]))

(defn- wrapp-with-dep [f deps]
  (fn [req]
    (f (merge req [:components deps]))))

(defn make-handler [routes deps]
  (-> routes
      (wrapp-with-dep deps)))

(defmacro defroutes [rec-name app-fn deps & routes]
  `(defrecord ~rec-name [~@deps]
     component/Lifecycle
     (start [this#]
       (let [keys# (map keyword '~deps)
             dep-map# (zipmap keys# ~deps)
             routes# (compojure/routes ~@routes)]
         (assoc this#
                :routes (make-handler routes# dep-map#)
                :app-fn app-fn )))
     (stop [this#] this#)))
