(ns system.schema
  (:require [schema.core :as s]))

(def PosInt 
  (s/both s/Int (s/pred pos?)))

(def Port
  (s/both s/Int (s/pred #(<= 0 % 65535))))

(def IpAddress
  s/Str)

(def Hostname
  s/Str)

(def FilePath
  s/Str)



