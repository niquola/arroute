(ns arroute.core
  (:require
    [clojure.zip :as z]
    [clojure.string :as str]))

(defn to-url
  "build url from pattern and params"
  [pattern & [params]]
  (->>
    (reduce
      (fn [acc item]
        (let [val (if (keyword? item) (item params) item)]
          (conj acc val)))
      [] pattern)
    (str/join "/")))

(defn- pathify [path] ;move to url
  (str/split path #"/"))

(defn match-url
  "match url by pattern and return hash of params"
  [url pattern]
  (let [url-parts (pathify url)
        pairs (map vector pattern url-parts)]
    (and
      (= (count url-parts) (count pattern))
      (reduce
        (fn [acc [v1 v2]]
          (if (or (= nil acc)
                  (and (not (keyword? v1))
                       (not (= v1 v2))))
            nil
            (if (keyword? v1) (assoc acc v1 v2) acc)))
        {}
        pairs))))

(defn def-routes
  "create routes zipper"
  [& rts]
  (z/zipper
    (constantly true)
    #(:nested %)
    (fn [node children] (assoc node :nested children))
    {:nested rts}))

(defn- current-path [zp-cur]
  (mapcat #(:path %) (conj (z/path zp-cur) (z/node zp-cur))))

(defn get-attr [zp attr-name]
"get attribute from node or his parent nodes"
  (loop [loc zp]
    (if (contains? (z/node loc) attr-name)
      (attr-name (z/node loc))
      (if (z/end? loc)
        nil
        (recur (z/up loc))))))

(defn find-route-rule
  "get routes-zipper and method path pair
  return zipper cursor if matched else nil"
  [routes-zipper [meth path]]
  (loop [loc routes-zipper]
    ; TODO: use pathified path for efficiency
    (if (and (match-url path (current-path loc))
             (= meth (get-attr loc :method)))
      loc
      (if (z/end? loc)
        nil
        (recur (z/next loc))))))
