(ns ns-tracker.nsdeps
  "Parsing namespace declarations for dependency information."
  (:use [clojure.set :only (union)]))

(defn- deps-from-libspec [prefix form]
  (cond (list? form) (apply union (map (fn [f] (deps-from-libspec
						(symbol (str (when prefix (str prefix "."))
							     (first form)))
						f))
				       (rest form)))
	(vector? form) (deps-from-libspec prefix (first form))
	(symbol? form) #{(symbol (str (when prefix (str prefix ".")) form))}
	(keyword? form) #{}
	:else (throw (IllegalArgumentException.
		      (pr-str "Unparsable namespace form:" form)))))

(defn- deps-from-ns-form [form]
  (when (and (list? form)
	     (contains? #{:use :require} (first form)))
    (apply union (map #(deps-from-libspec nil %) (rest form)))))

(defn deps-from-ns-decl
  "Given a (quoted) ns declaration, returns a set of symbols naming
  the dependencies of that namespace.  Handles :use and :require clauses."
  [decl]
  (apply union (map deps-from-ns-form decl)))
