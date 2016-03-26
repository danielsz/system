(ns system.components.sente
  #?(:clj
     (:require [com.stuartsierra.component :as component]
               [compojure.core :refer [routes GET POST]]
               [taoensso.sente :as sente])

     :cljs
     (:require [com.stuartsierra.component :as component]
               [taoensso.sente :as sente])))
#?(:clj
   (defn sente-routes [{{ring-ajax-post :ring-ajax-post ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake} :sente}]
     (routes
      (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
      (POST "/chsk" req (ring-ajax-post                req)))))

;; Sente supports both CLJ and CLJS as a server
(defrecord ChannelSocketServer [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! connected-uids router web-server-adapter handler options]
  component/Lifecycle
  (start [component]
    (let [handler (or handler (get-in component [:sente-handler :handler]))
          {:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
          (sente/make-channel-socket-server! web-server-adapter options)]
      (assoc component
        :ring-ajax-post ajax-post-fn
        :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
        :ch-chsk ch-recv
        :chsk-send! send-fn
        :connected-uids connected-uids
        :router (atom (sente/start-chsk-router! ch-recv (if (:wrap-component? options)
                                                          (handler component)
                                                          handler))))))
  (stop [component]
    (if-let [stop-f (and router @router)]
      (assoc component :router (stop-f))
      component)))

(defn new-channel-socket-server
  ([web-server-adapter]
   (new-channel-socket-server nil web-server-adapter {}))
  ([event-msg-handler web-server-adapter]
   (new-channel-socket-server event-msg-handler web-server-adapter {}))
  ([event-msg-handler web-server-adapter options]
   (map->ChannelSocketServer {:web-server-adapter web-server-adapter
                              :handler event-msg-handler
                              :options options})))

;; Old fn name
(def new-channel-sockets new-channel-socket-server)

;; Sente does not support CLJ as a client yet
#?(:cljs
   (defrecord ChannelSocketClient [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! connected-uids router path handler options]
     component/Lifecycle
     (start [component]
       (let [handler (or handler (get-in component [:sente-handler :handler]))
             {:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
             (sente/make-channel-socket-client! path options)]
         (assoc component
                :ring-ajax-post ajax-post-fn
                :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
                :ch-chsk ch-recv
                :chsk-send! send-fn
                :connected-uids connected-uids
                :router (atom (sente/start-chsk-router! ch-recv (if (:wrap-component? options)
                                                                  (handler component)
                                                                  handler))))))
     (stop [component]
       (if-let [stop-f (and router @router)]
         (assoc component :router (stop-f))
         component))))

#?(:cljs
   (defn new-channel-socket-client
     ([path]
      (new-channel-socket-client nil path {}))
     ([event-msg-handler path]
      (new-channel-socket-client event-msg-handler path {}))
     ([event-msg-handler path options]
      (map->ChannelSocketClient {:path    path
                                 :handler event-msg-handler
                                 :options options}))))
