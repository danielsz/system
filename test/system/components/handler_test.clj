(ns system.components.handler-test
  (:require [clojure.test :refer :all]

            [com.stuartsierra.component :as component]

            [system.components.endpoint :refer [new-endpoint]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.jetty :refer [new-web-server]]
            [system.components.handler :refer [new-handler]]

            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]

            [compojure.core :as compojure :refer [GET]]
            [compojure.route :refer [not-found]]

            [bidi.ring :as bidi :refer (make-handler)]
            [ring.util.response :as res]

            [system.components.handler :as h]
            [lang-utils.core :refer [contains+? ∘]]))



(defn config []
  {:http-port  (Integer. 8081)
   :middleware [[wrap-defaults api-defaults]
                wrap-gzip]})


(defn index-handler
  [request]
  (res/response "Homepage"))


(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))


(defn home-routes [endpoint]
  ["/" {"index.html" index-handler
        ["articles/" :id "/article.html"] article-handler}])


(defn app-system [config]
  (component/system-map
    :routes     (new-endpoint home-routes)
    :middleware (new-middleware {:middleware (:middleware config)})
    :handler    (-> (new-handler :bidi)
                    (component/using [:routes :middleware]))
    :http       (-> (new-web-server (:http-port config))
                    (component/using [:handler]))))


(comment
  (def c (-> (config)
             app-system
             component/start))








  )
