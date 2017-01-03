(ns system.components.middleware
  (:require [com.stuartsierra.component :as component]
            [lang-utils.core :refer [∘]]))

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
(defn- compose [entries]
  (apply ∘ (map middleware-fn (reverse entries))))

;; allow middleware to wrap the component
(defn- sanitize [component entry]
  (if (vector? entry)
    (replace {:component component} entry)
    entry))

(defrecord Middleware [middleware]
  component/Lifecycle
  (start [component]
    (let [sanitize (partial sanitize component)
          entries (mapv sanitize (:middleware middleware))
          wrap-mw (compose entries)]
      (assoc component :wrap-mw wrap-mw)))
  (stop [component]
    (dissoc component :wrap-mw)))

(defn new-middleware
  ([middleware] (->Middleware middleware)))
