Infixing
================================================================================

Examples
--------------------------------------------------------------------------------

.. code:: sh

  lein repl

.. code:: clojure

  (require 'infixing)
  (defmacro try-infixing [code]
    (infixing (rules $-rule where-rule) code))
  (try-infixing (println $ + 1 1))                   ; => 2
  (try-infixing (println x where [x "hello wprld"])) ; => hello wprld
  (try-infixing (println $ + x x where [x 1]))       ; => 2
