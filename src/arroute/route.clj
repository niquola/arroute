(ns arroute.route
  (:require
    [clojure.string :as str]
    [arroute.url :as u]
    [clojure.zip :as z]))

(defn def-routes "create routes zipper"
  [& rts]
  (z/zipper
    (constantly true)
    #(:nested %)
    (fn [node children] (assoc node :nested children))
    {:nested rts}))

(defn- pathify [path] ;move to url
  (str/split path #"/"))

(defn- current-path [zp-cur]
  (mapcat #(:path %) (conj (z/path zp-cur) (z/node zp-cur))))

(defn find-route-rule
  "get routes-zipper and method path pair
  return zipper cursor if matched else nil"
  [routes-zipper [meth path]]
  (loop [loc routes-zipper]
    ; TODO: use pathified path for efficiency
    (if (and (u/match-url path (current-path loc))
             (= meth (get-attr loc :method)))
      loc
      (if (z/end? loc)
        nil
        (recur (z/next loc))))))

(defn get-attr [zp attr-name]
"get attribute from node or his parent nodes"
  (loop [loc zp]
    (if (contains? (z/node loc) attr-name)
      (attr-name (z/node loc))
      (if (z/end? loc)
        nil
        (recur (z/up loc))))))
