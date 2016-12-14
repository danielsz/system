(ns system.components.middleware
  (:require [com.stuartsierra.component :as component]))

; vector of vectors
#_ (defn- middleware-fn2 [entry]
  (if (seq (rest entry))
    #(apply (first entry) % (rest entry))
    (first entry)))

; vector of functions or vectors
(defn- middleware-fn [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

;; explanation for reverse https://github.com/duct-framework/duct/issues/31#issuecomment-171459482
(defn- compose-middleware [middleware]
  (let [entries (:middleware middleware)]
    (apply comp (map middleware-fn (reverse entries)))))

(defrecord Middleware [middleware]
  component/Lifecycle
  (start [component]
    (let [wrap-mw (compose-middleware middleware)]
      (assoc component :wrap-mw wrap-mw)))
  (stop [component]
    (dissoc component :wrap-mw)))

(defn new-middleware
  ([middleware] (->Middleware middleware)))
