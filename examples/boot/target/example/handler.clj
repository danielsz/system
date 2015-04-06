(ns example.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [example.html :as html]))

(defroutes routes
  (GET "/" [] (html/index))
  (route/not-found (html/index)))

(def app
  (-> routes 
      (wrap-defaults site-defaults)))
