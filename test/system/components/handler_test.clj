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

(defn index-handler
  [request]
  (res/response "Homepage"))

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(defn home-routes [endpoint]
  ["/" {"index.html" index-handler
        ["articles/" :id "/article.html"] article-handler}])

(def bidi-system
  (component/system-map
    :routes     (new-endpoint home-routes)
    :middleware (new-middleware {:middleware [[wrap-defaults api-defaults]
                                              wrap-gzip]})
    :handler    (-> (new-handler :router :bidi)
                    (component/using [:routes :middleware]))
    :http       (-> (new-web-server 8081)
                    (component/using [:handler]))))
;; =============================================================================
;; End of bidi system setup


(defn bidi-fixtures [f]
  (alter-var-root #'bidi-system component/start)
  (f)
  (alter-var-root #'bidi-system component/stop))

(use-fixtures :each bidi-fixtures)

(deftest check-instance
  (is (instance? system.components.endpoint.Endpoint (:routes bidi-system)))
  (is (instance? system.components.middleware.Middleware (:middleware bidi-system)))
  (is (instance? system.components.handler.Handler (:handler bidi-system))))

(deftest bidi
  (is (= #'bidi.ring/make-handler (get-in bidi-system [:handler :router])))
  (is (= (home-routes nil)
         (get-in bidi-system [:handler :routes :routes]))))
