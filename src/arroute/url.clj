(ns arroute.url
  (:require
    [clojure.string :as str]))

(defn to-url [pattern & [params]]
  (->>
    (reduce
      (fn [acc item]
        (let [val (if (keyword? item) (item params) item)]
          (conj acc val)))
      [] pattern)
    (str/join "/")))

(defn match-url [url pattern]
  (let [url-parts (str/split url #"/")
        pairs (map vector pattern url-parts)]
    (and
      (= (count url-parts) (count pattern))
      (reduce
        (fn [acc [v1 v2]]
          (if (or (= false acc)
                  (and (not (keyword? v1))
                       (not (= v1 v2))))
            false
            (if (keyword? v1) (assoc acc v1 v2) acc)))
        {}
        pairs))))
