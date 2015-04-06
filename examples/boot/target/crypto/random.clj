(ns crypto.random
  "Cryptographically secure random numbers and strings."
  (:refer-clojure :exclude [bytes])
  (:require [clojure.string :as string])
  (:import java.security.SecureRandom
           [org.apache.commons.codec.binary Base64 Base32 Hex]))

(defn bytes
  "Returns a random byte array of the specified size."
  [size]
  (let [seed (byte-array size)]
    (.nextBytes (SecureRandom.) seed)
    seed))

(defn base64
  "Return a random base64 string of the specified size in bytes."
  [size]
  (String. (Base64/encodeBase64 (bytes size))))

(defn base32
  "Return a random base32 string of the specified size in bytes."
  [size]
  (.encodeAsString (Base32.) (bytes size)))

(defn hex
  "Return a random hex string of the specified size in bytes."
  [size]
  (String. (Hex/encodeHex (bytes size))))

(defn url-part
  "Return a random string suitable for being inserted into URLs. The size
  denotes the number of bytes to generate."
  [size]
  (-> (base64 size)
      (string/replace "+" "-")
      (string/replace "/" "_")
      (string/replace "=" "")))
