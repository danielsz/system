(ns system.components.sente
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [taoensso.sente :as sente]))

(defn sente-routes [{{ring-ajax-post :ring-ajax-post ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake} :sente}]
  (routes
   (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
   (POST "/chsk" req (ring-ajax-post                req))))

(defrecord ChannelSockets [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! connected-uids router server-adapter event-msg-handler options]
  component/Lifecycle
  (start [component]
    (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
          (sente/make-channel-socket! server-adapter options)]
      (assoc component
        :ring-ajax-post ajax-post-fn
        :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
        :ch-chsk ch-recv
        :chsk-send! send-fn
        :connected-uids connected-uids
        :router (atom (sente/start-chsk-router! ch-recv (if (:wrap-component? options)
                                                          (event-msg-handler component)
                                                          event-msg-handler))))))
  (stop [component]
    (if-let [stop-f @router]
      (assoc component :router (stop-f))
      component)))

(defn new-channel-sockets
  ([event-msg-handler server-adapter]
   (new-channel-sockets event-msg-handler server-adapter {}))
  ([event-msg-handler server-adapter options]
   (map->ChannelSockets {:server-adapter server-adapter
                         :event-msg-handler event-msg-handler
                         :options options})))


