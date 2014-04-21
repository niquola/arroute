(ns arroute.core-test
  (:use midje.sweet)
  (:use arroute.core))


(fact "pathify"
      (pathify "/") => []
      (pathify "/users/") => ["users"]
      (pathify "users/") => ["users"]
      (pathify "users") => ["users"]
      (pathify "users/1/show") => ["users" "1" "show"])

(fact "represent url as array"
      (def users-profile ["users" :id "profile"])
      (def posts ["blog" :user "posts" :filter])

      (to-url users-profile {:id 1})  => "users/1/profile"
      (match-url "/" []) => {}
      (match-url "unexisting/1/profile" users-profile) => nil
      (match-url "users/1/profile" users-profile) => {:id "1"}
      (match-url "users/nicola/profile" users-profile) => {:id "nicola"}
      (match-url "blog/nicola/posts/last" posts) => {:filter "last" :user "nicola"})

(def routes
  (def-routes
    {:path []
     :nested
     [{:path [] :method :get :name "root"}
      {:path ["users"] :ns 'users
       :nested
       [{:method :get :desc "List users"}
        {:method :post :desc "Create user" :auth [:admin]}
        {:path [:id]
         :nested
         [{:method :get :desc "Show user"}
          {:method :put :desc "Update user"}
          {:method :post :path ["activate"] :action 'activate} ]}]}]}))

(fact "route is hash-map & can be nested"
      (find-route-rule
        routes
        [:post "user/5/activate"])  => nil

      (get-attr
        (find-route-rule
          routes
          [:post "users"])
        :desc)  => "Create user"

      (get-attr
        (find-route-rule
          routes
          [:get "/"])
        :name)  => "root"

      (get-attr
        (find-route-rule
          routes [:post "users/5/activate"])
        :action)  => 'activate)
