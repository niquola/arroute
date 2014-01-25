(ns arroute.core-test
  (:use midje.sweet)
  (:use arroute.core))


(def routes
  [[:get ["posts"] {:meta "meta"}
    [[:post [] {} []]
     [:get [:id] {}
      [[:put [] {} []]
       [:delete [] {} []]
       [:get ["comments"] {} []]]]]]
   [:get ["users"] {}
    [[:post [] {} []]
     [:get [:id] {} []]]]
   [:get ["dashboard"] {} []]])

(facts
  "match routes"
  (match-route (pathify "/users/1/once/2") ["users" :id "once" :uid]) => {:id "1" :uid "2"}
  (match-route (pathify "/users/1/once") ["users" :id "once" :uid]) => false
  (fact "pathify" (pathify "/users/1") => ["users" "1"]))

(facts "flatten"
       (count (flatten-routes routes)) => 10)

(fact "dispatch"
      (dispatch {:uri "/posts/1" :method :put} routes)  => [:put ["posts" :id] {} []])

(facts
  "DSL"
  (fact "get"
        (GET "url") => [:get ["url"] {} []]
        (GET ["url"]) => [:get ["url"] {} []]
        (GET ["url"] {:meta "meta"}) => [:get ["url"] {:meta "meta"} []]
        (GET ["url"] ["nested"] ["nested2"]) => [:get ["url"] {} [["nested"] ["nested2"]]]

        (POST "url") => [:post ["url"] {} []]
        )

  (fact
    "res"
    (resources "users")
    => [:get ["users"] {}
        [
         [:post [] {} []]
         [:get [:id] {}
          [
           [:put [] {} []]
           [:delete [] {} []]
           ]]]])

  (def xroutes
    [(resources "users")]))
