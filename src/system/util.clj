(ns system.util)

(defn assert-options! [component-name options allowed-opts]
  (let [invalid-keys (keys (apply dissoc options allowed-opts))]
    (assert (not invalid-keys)
            (format "Invalid option(s) for %s: %s"
                    component-name (pr-str invalid-keys)))))
