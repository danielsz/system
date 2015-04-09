(ns example.html
  (:require 
   (hiccup [page :refer [html5 include-js include-css]])))

(defn index []
  (html5
   [:head 
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:meta {:name "description" :content "System"}]
    [:meta {:name "author" :content "Daniel Szmulewicz"}]
    [:title "System"]

    (include-css 
     "//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
     "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"
     "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css")

    "<!--[if lt IE 9]>"
    [:script {:src "https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"}]
    [:script {:src "https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"}]
    "<![endif]-->"]
   [:body 
    
    [:div#main-area.container
     [:p "This is an example project for system."]]
    
    
    (include-js 
     "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"
     "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js")]))
