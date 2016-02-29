(ns system.schema
  (:require [schema.core :as s]))

(def PosInt 
  (s/both s/Int (s/pred pos?)))

(defn port-range-inclusive [min max]
  (s/both s/Int 
          (s/both (s/pred #(<= min %) `(~'ge ~min)) 
                  (s/pred #(<= % max) `(~'le ~max)))))

(def Port
  (port-range-inclusive 0 65535))

(def IpAddress
  s/Str)

(def Hostname
  s/Str)

(def FilePath
  s/Str)



