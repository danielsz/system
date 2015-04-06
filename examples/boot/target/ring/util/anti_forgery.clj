(ns ring.util.anti-forgery
  "Utility functions for inserting anti-forgery tokens into HTML forms."
  (:use [hiccup core form]
        ring.middleware.anti-forgery))

(defn anti-forgery-field
  "Create a hidden field with the session anti-forgery token as its value.
  This ensures that the form it's inside won't be stopped by the anti-forgery
  middleware."
  []
  (html (hidden-field "__anti-forgery-token" *anti-forgery-token*)))
