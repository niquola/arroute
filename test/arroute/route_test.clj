(ns arroute.route-test
  (:use midje.sweet)
  (:use arroute.route))


(def routes
  (def-routes
    {:path ["users"] :ns 'users
     :nested [{:method :GET :desc "List users"}
              {:method :POST :desc "Create user" :auth [:admin]}
              {:path [:id]
               :nested
               [{:method :GET :desc "Show user"}
                {:method :PUT :desc "Update user"}
                {:method :POST :path ["activate"] :action 'activate}]
               }
              ]}
    ))

(fact "route is hash-map & can be nested"
      (find-route-rule
        routes
        [:POST "user/5/activate"])  => nil

      (get-attr
        (find-route-rule
          routes [:POST "users/5/activate"])
        :action
        )  => 'activate)
