(ns ring.middleware.proxy-headers
  "Middleware for handling headers set by HTTP proxies."
  (:require [clojure.string :as str]))

(defn wrap-forwarded-remote-addr
  "Middleware that changes the :remote-addr of the request map to the
  first value present in the X-Forwarded-For header."
  [handler]
  (fn [request]
    (if-let [forwarded-for (get-in request [:headers "x-forwarded-for"])]
      (let [remote-addr (str/trim (re-find #"^[^,]*" forwarded-for))]
        (handler (assoc request :remote-addr remote-addr)))
      (handler request))))
