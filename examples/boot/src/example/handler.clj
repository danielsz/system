(ns example.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [response content-type]]
   [example.html :as html]))

(defroutes routes
  (GET "/" [] (html/index))
  (GET "/bar" [] (html/index))
  (GET "/test" [] (-> (response "Testing.")
                      (content-type "text/html")))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> #'routes
      (wrap-defaults site-defaults)))

