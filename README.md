# arroute

A Clojure library designed to deal with routes as data.

Represent routes as tree of clojure data structures

```clojure
; [method url meta nested-routes]

[[:get ["posts"] {:meta "meta"}
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

For more information see `tests/`

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
