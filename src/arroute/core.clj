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

(defn pathify [path]
  (filterv #(not (str/blank? %))
           (str/split path #"/")))

(defn- pair-matched [k v]
  (or (keyword? k) (= k v)))

(defn- assoc-acc [a k v]
  (if (keyword? k) (assoc a k v) a))

(defn match-path [path pattern]
  (and
    (= (count path) (count pattern))
    (loop [a {}
           [v & path] path
           [k & pattern] pattern]
      (cond
        (not (pair-matched k v)) nil
        (and (empty? path) (empty? pattern)) (assoc-acc a k v)
        :else (recur (assoc-acc a k v) path pattern)))))

(defn match-url
  "match url by pattern and return hash of params"
  [url pattern]
  (match-path (pathify url) pattern))

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
    (when-let [node (and loc (z/node loc))]
      (println (keys node))
      (if (contains? node attr-name)
        (attr-name node)
        (when (not (z/end? loc))
          (recur (z/up loc)))))))

(defn find-route-rule
  "get routes-zipper and method path pair
  return zipper cursor if matched else nil"
  [routes-zipper [meth uri]]
  (let [path (pathify uri)]
    (loop [loc routes-zipper]
      (cond
        (and (match-path path (current-path loc))
             (= meth (:method (z/node loc)))) loc
        (z/end? loc) nil
        :else (recur (z/next loc))))))
