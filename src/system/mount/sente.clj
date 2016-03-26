(ns system.mount.sente
  (:require [system.mount :refer [defstate config]]
            [compojure.core :refer [routes GET POST]]
            [taoensso.sente :as sente]))

(defstate channel-sockets
  :start (let [handler (get-in config [:sente :handler])
               options (get-in config [:sente :options])
               sock (sente/make-channel-socket! (get-in config [:sente :server-adapter]) options)
               router (sente/start-chsk-router! (:ch-recv sock)
                                                (if (:wrap-component? options)
                                                  (handler sock)
                                                  handler))]
           (assoc sock :router router))
  :stop ((:router channel-sockets)))

(defn sente-routes []
  (let [handshake-fn (:ajax-get-or-ws-handshake-fn channel-sockets)
        post-fn (:ajax-post-fn channel-sockets)]
    (routes
     (GET  "/chsk" req (handshake-fn req))
     (POST "/chsk" req (post-fn req)))))
