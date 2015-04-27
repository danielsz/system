(ns example.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [example.html :as html]))

(defroutes routes
  (GET "/" [] (html/index))
  (GET "/bar" [] (html/index))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> routes 
      (wrap-defaults site-defaults)))
