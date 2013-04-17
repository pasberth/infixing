Infixing
================================================================================

Examples
--------------------------------------------------------------------------------

```sh
lein repl
```

```clojure
(require 'infixing)
(defmacro try-infixing [code]
  (infixing (rules $-rule where-rule) code))
(try-infixing (println $ + 1 1))                   ; => 2
(try-infixing (println x where [x "hello wprld"])) ; => hello wprld
```
