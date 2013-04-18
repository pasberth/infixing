Infixing
================================================================================

Examples
--------------------------------------------------------------------------------

.. code:: sh

  lein repl

.. code:: clojure

  (use 'infixing.core)
  (use 'infixing.control.prelude)
  (defmacro try-infixing [code]
    (infixing (rules $-rule where-rule ->-rule) code))
  (try-infixing (println $ + 1 1))                           ; => 2
  (try-infixing (println x where [x "hello world"]))         ; => hello world
  (try-infixing (println $ + x x where [x 1]))               ; => 2
  (try-infixing (let [f #(+ 2 %) g #(+ 3 %)] (1 -> f -> g))) ; => 6
