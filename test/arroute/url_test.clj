(ns arroute.url-test
  (:use midje.sweet)
  (:use arroute.url))


(fact "let's getting started
       we represent url as array"

       (def users-profile ["users" :id "profile"])
       (def posts ["blog" :user "posts" :filter])

       (to-url users-profile {:id 1})  => "users/1/profile"
       (match-url "users/1/profile" users-profile) => {:id "1"}
       (match-url "users/nicola/profile" users-profile) => {:id "nicola"}
       (match-url "blog/nicola/posts/last" posts) => {:filter "last" :user "nicola"}
       )
