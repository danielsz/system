(ns ns-tracker.parse
  (:use [clojure.tools.namespace.parse :only (comment?)]))

(defn in-ns-decl?
  "Returns true if form is a (in-ns ...) declaration."
  [form]
  (and (list? form) (= 'in-ns (first form))))

(defn read-in-ns-decl
  "Attempts to read a (in-ns ...) declaration from a java.io.PushbackReader.
  Returns nil if it fails or if a in-ns declaration cannot be found. The in-ns
  declaration must be the first Clojure form in the file, except for
  (comment ...) forms"
  [rdr]
  (try
    (loop []
      (let [form (doto (read rdr) str)]
        (cond
         (in-ns-decl? form) form
         (comment? form) (recur)
         :else nil)))
       (catch Exception e nil)))
