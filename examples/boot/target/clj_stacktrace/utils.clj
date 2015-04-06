(ns clj-stacktrace.utils)

(defn rjust
  "If width is greater than the length of s, returns a new string
  of length width with s right justified within it, otherwise returns s."
  [width s]
  (format (str "%" width "s") s))

(defn quartile1
  "Compute the first quartile for the given collection according to
  Tukey (Hoaglin et al. 1983). coll must be sorted."
  ;; Hoaglin, D.; Mosteller, F.; and Tukey, J. (Ed.).
  ;; Understanding Robust and Exploratory Data Analysis.
  ;; New York: Wiley, pp. 39, 54, 62, 223, 1983.
  [coll]
  (let [c (count coll)]
    (nth coll (if (even? c)
                (/ (+ c 2) 4)
                (/ (+ c 3) 4)))))

(defn quartile3
  "Compute the third quartile for the given collection according to
  Tukey (Hoaglin et al. 1983). coll must be sorted."
  ;; Hoaglin, D.; Mosteller, F.; and Tukey, J. (Ed.).
  ;; Understanding Robust and Exploratory Data Analysis.
  ;; New York: Wiley, pp. 39, 54, 62, 223, 1983.
  [coll]
  (let [c (count coll)]
    (nth coll (if (even? c)
                (/ (+ (* 3 c) 2) 4)
                (/ (inc (* 3 c)) 4)))))

(defn fence
  "Compute the upper outer fence for the given coll. coll must be sorted."
  [coll]
  (let [q1  (quartile1 coll)
        q3  (quartile3 coll)
        iqr (- q3 q1)]
    (int (+ q3 (/ (* 3 iqr) 2)))))
