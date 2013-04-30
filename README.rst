Infixing
================================================================================

Examples
--------------------------------------------------------------------------------

.. code:: sh

   CLASSPATH=$PWD/src clojure-1.4 examples/example.clj

.. code:: clojure

  (use 'infixing.core)

  (defn $ [f x]
    (f x))
  (defmacro where [a b]
    `(let ~b ~a))

  (defmacro try-infixing [code]
    (infixing (rules (infixr 0 '$)
                     (infix -1 'where)
                     (infixl 1 '->))
              code))

  (try-infixing (println $ (+ 1 1)))                                   ; => 2
  (try-infixing ((println x) where [x "hello world"]))                 ; => hello world
  (try-infixing (println $ (+ x x) where [x 1]))                       ; => 2
  (try-infixing (println $ 1 -> f -> g where [f #(+ 2 %) g #(+ 3 %)])) ; => 6