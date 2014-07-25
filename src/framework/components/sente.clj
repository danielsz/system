(ns framework.components.sente
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente :as sente]))

(defrecord ChannelSockets [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! chsk-router handler]
  component/Lifecycle
  (start [component]
    (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! {})]
      (assoc component 
        :ring-ajax-post ajax-post-fn 
        :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
        :ch-chsk ch-recv
        :chsk-send! send-fn
        :chsk-router (sente/start-chsk-router-loop! handler ch-recv))))
  (stop [component]
    ((:chsk-router component))
    component))

(defn new-channel-sockets
  [handler]
  (map->ChannelSockets {:handler handler}))


