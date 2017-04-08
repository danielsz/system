(ns system.schema
  (:require [schema.core :as s]))

(def PosInt 
  (s/both s/Int (s/pred pos?)))

(defn port-range-inclusive?
  [min max]
  (fn [i] (and (integer? i)
               (<= min i max))))

(def Port
  (let [min-valid-port 0
        max-valid-port 65535]
    (s/pred (port-range-inclusive? min-valid-port max-valid-port)
            (str  "An integer p such that " min-valid-port " <= p <= " max-valid-porto))))

(def IpAddress
  s/Str)

(def Hostname
  s/Str)

(def FilePath
  s/Str)



