(ns arroute.core-test
  (:use midje.sweet)
  (:use arroute.core))

(fact "represent url as array"

       (def users-profile ["users" :id "profile"])
       (def posts ["blog" :user "posts" :filter])

       (to-url users-profile {:id 1})  => "users/1/profile"
       (match-url "unexisting/1/profile" users-profile) => nil
       (match-url "users/1/profile" users-profile) => {:id "1"}
       (match-url "users/nicola/profile" users-profile) => {:id "nicola"}
       (match-url "blog/nicola/posts/last" posts) => {:filter "last" :user "nicola"}
       )

(def routes
  (def-routes
    {:path ["users"] :ns 'users
     :nested [{:method :GET :desc "List users"}
              {:method :POST :desc "Create user" :auth [:admin]}
              {:path [:id]
               :nested
               [{:method :GET :desc "Show user"}
                {:method :PUT :desc "Update user"}
                {:method :POST :path ["activate"] :action 'activate}
                ]}
              ]}
    ))

(fact "route is hash-map & can be nested"
      (find-route-rule
        routes
        [:POST "user/5/activate"])  => nil

      (get-attr
        (find-route-rule
          routes [:POST "users/5/activate"])
        :action)  => 'activate)

