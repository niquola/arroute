(ns arroute.core-test
  (:require [arroute.map :as am])
  (:use midje.sweet))


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
  {:attrs  {:filters [0]}
   :get    {:filter [1] :desc "Root"}
   "posts" {:attrs {:roles #{:*}}
            :get       {:desc "List posts"}
            :post      {:desc "Create post"}
            [:id]   {:get       {:desc "Show post"}
                     :post      {:desc "Update post"}
                     "publish"  {:post {:desc "Publish post"}}}}
   "users" {:attrs {:roles #{:admin}}
            :get       {:desc "List users"}
            :post      {:desc "Create user" :roles #{:admin}}
            "active"   {:get {:desc "Filtering users"}}

            [:id]   {:get       {:desc "Show user"}
                     :post      {:desc "Update user"}
                     "activate" {:post {:desc "Activate"}}}}})


(am/match [:get "some/unexistiong/route"] routes)

(fact "route is hash-map & can be nested"
      (:desc
        (am/match [:get "users/active"] routes)) => "Filtering users"

      (:desc
        (am/match [:get "/"] routes)) => "Root"

      (:desc
        (am/match [:post "users/5/activate"] routes)) => "Activate"

      (:params
        (am/match [:post "users/5/activate"] routes))
      => {:id "5"}

      (count
        (:parents
          (am/match [:post "users/5/activate"] routes)))
      => 4)

