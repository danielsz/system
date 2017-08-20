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


;; =============================================================================
;; Bidi system set up
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


(defn bidi-system [config]
  (component/system-map
    :routes     (new-endpoint home-routes)
    :middleware (new-middleware {:middleware (:middleware config)})
    :handler    (-> (new-handler :bidi)
                    (component/using [:routes :middleware]))
    :http       (-> (new-web-server (:http-port config))
                    (component/using [:handler]))))
;; =============================================================================
;; End of bidi system setup

(def bs (bidi-system (config)))

(defn bidi-fixtures [f]
  (alter-var-root #'bs component/start)
  (f)
  (alter-var-root #'bs component/stop))

(use-fixtures :each bidi-fixtures)

(deftest check-instance
  (is (instance? system.components.endpoint.Endpoint (:routes bs)))
  (is (instance? system.components.middleware.Middleware (:middleware bs)))
  (is (instance? system.components.handler.Handler (:handler bs))))

(deftest bidi
  (is (= #'bidi.ring/make-handler (get-in bs [:handler :router])))
  (is (= (home-routes nil)
         (get-in bs [:handler :routes :routes]))))
