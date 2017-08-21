(ns system.components.handler-test
  (:require [clojure.test :refer :all]

            [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.jetty :refer [new-web-server]]
            [system.components.handler :refer [new-handler]]

            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.mock.request :as mock]

            [compojure.core :as compojure :refer [GET routes]]
            [compojure.route :refer [not-found]]

            [bidi.ring :as bidi :refer (make-handler)]
            [ring.util.response :as res]))


;; =============================================================================
;; Utilities

(defn index-handler
  [request]
  (res/response "Homepage"))

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(defn bidi-routes [_]
  ["/" {"index.html" index-handler
        ["articles/" :id "/article.html"] article-handler}])

(defn compojure-routes [_]
  (routes
   (GET "/" [] (-> (res/response "Example.")
                   (res/content-type "text/plain")))
   (not-found "<h1>Page not found</h1>")))

(defn system [port router routes]
  (component/system-map
    :routes     (new-endpoint routes)
    :middleware (new-middleware {:middleware [[wrap-defaults api-defaults]]})
    :handler    (-> (new-handler :router router)
                    (component/using [:routes :middleware]))
    :http       (-> (new-web-server port)
                    (component/using [:handler]))))
;; =============================================================================
;; End of bidi system setup

(def bidi-system (system 8081 :bidi bidi-routes))
(def compojure-system (system 8082 :compojure compojure-routes))

(defn fixtures [f]
  (alter-var-root #'bidi-system component/start)
  (alter-var-root #'compojure-system component/start)
  (f)
  (alter-var-root #'compojure-system component/stop)
  (alter-var-root #'bidi-system component/stop))

(use-fixtures :each fixtures)

(deftest check-instance
  (is (instance? com.stuartsierra.component.SystemMap compojure-system))
  (is (instance? com.stuartsierra.component.SystemMap bidi-system))
  (is (instance? system.components.endpoint.Endpoint (:routes bidi-system)))
  (is (instance? system.components.middleware.Middleware (:middleware bidi-system)))
  (is (instance? system.components.handler.Handler (:handler bidi-system)))
  (is (instance? system.components.endpoint.Endpoint (:routes compojure-system)))
  (is (instance? system.components.middleware.Middleware (:middleware compojure-system)))
  (is (instance? system.components.handler.Handler (:handler compojure-system))))

(deftest router
  (is (= #'bidi.ring/make-handler (get-in bidi-system [:handler :router])))
  (is (= #'compojure/routes (get-in compojure-system [:handler :router]))))

(deftest handler
  (is (= ((:handler (:handler compojure-system)) (mock/request :get "/"))
         {:status  200
          :headers {"Content-Type" "text/plain; charset=utf-8"}
          :body    "Example."}))
  (is (= ((:handler (:handler bidi-system)) (mock/request :get "/index.html"))
         {:status  200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body "Homepage"})))

(deftest default-system-is-compojure
  (let [default-system (component/system-map
                        :routes     (new-endpoint compojure-routes)
                        :middleware (new-middleware {:middleware [[wrap-defaults api-defaults]]})
                        :handler    (-> (new-handler)
                                        (component/using [:routes :middleware]))
                        :http       (-> (new-web-server 8083)
                                        (component/using [:handler])))]
    (is (instance? com.stuartsierra.component.SystemMap default-system))
    (is (= #'compojure/routes (get-in default-system [:handler :router])))
    (is (= ((:handler (:handler (component/start default-system))) (mock/request :get "/"))
           {:status  200
            :headers {"Content-Type" "text/plain; charset=utf-8"}
            :body    "Example."}))
    (component/stop default-system)))
