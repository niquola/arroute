(ns arroute.core
  (:require
    [clojure.string :as str]))

(defn get-meth [x] (get x 0))
(defn get-route [x] (second x))
(defn get-nested [x] (last x))
(defn rm-nested [x] (assoc x 3 []))

(defn- build-route [br r]
  (let [base-uri (get-route br)
        uri      (concat base-uri (get-route r))]
    (rm-nested (assoc r 1 uri))))

(defn flatten-routes
  ([r] (flatten-routes [] [r]))
  ([br r]
   (mapcat
     (fn [sr]
       (let [bsr (build-route br sr)]
         (cons bsr (flatten-routes bsr sr))))
     (get-nested r))))


(defn pathify [pth]
  (filter
    (fn [x] (not (empty? x)))
    (str/split pth #"/")))

(defn match-route [pth route]
  (let [pairs (zipmap route pth)]
    (if (and (every?  (fn [[k v]] (or (keyword? k)(= k v))) pairs)
             (= (count pth) (count route)))
      (apply hash-map (apply concat (filter (fn [[k v]] (keyword? k)) pairs)))
      false)))

(defn dispatch [request routes]
  (first (filter
           (fn [route]
             (and
               (= (or (:method request) (:request-method request)) (get-meth route))
               (match-route
                 (pathify (:uri request))
                 (get-route route))))
           (flatten-routes routes))))

(defn mk-meth [m url & args]
  (let [url (if (vector? url) url [url])
        meta? (first args)
        meta (if (map? meta?) meta? {})
        nested-list (if (map? meta?) (rest args) args)
        nested (vec nested-list)]
    [m url meta nested]))

(defmacro def.meth [fnm]
  (let [knm (keyword (str/lower-case (str fnm)))]
    `(defn ~fnm
       ([url#] (mk-meth ~knm url# {}))
       ([url# & args#] (apply mk-meth ~knm url# args#)))))


(def.meth GET)
(def.meth POST)
(def.meth PUT)
(def.meth PATCH)
(def.meth DELETE)
(def.meth HEAD)

(defn resources [name]
  (GET name
       (POST [])
       (GET :id
            (PUT [])
            (DELETE []))))
