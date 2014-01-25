# arroute

A Clojure library designed to deal with routes as data.

Routes are just clojure data structure, which means:

* Composable
* Transformabe
* Can be used as meta information

Dispatching is just search in tree
wich allow you any customization and full control.

### Basic API

Represent url pattern as vector:

```clojure
"/users/1/profile/" => ["users" :id "profile"]
```

Represent routes as tree of clojure data structures

```clojure
; [method url meta nested-routes]

[[:get ["posts"] {:meta "u can put any information here"}
  [[:post [] {} []]
   [:get [:id] {}
    [[:put [] {} []]
     [:delete [] {} []]
     [:get ["comments"] {} []]]]]]]

```

Dispatching becomes just search in tree and can be done as

```clojure
(dispatch {:uri "/posts/1" :method :put} routes)  => [:put ["posts" :id] {} []])
```

### DSL

To remove boilerplate you can use (or create your own dsl):

```clojure
(GET "url/:id" {:meta "any info"}) => [:get ["url" :id] {:meta "any info"} []]
(resources "users")
    => (GET name
         (POST [])
         (GET :id
              (PUT [])
              (DELETE [])))

(GET "/posts"
  (POST [])
  (GET "popular")
  (GET :id
    (PUT [])
    (GET "comments" )))

```

For more information see `tests/`

## TODO

* generate named routes
* usage in ring application example

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
