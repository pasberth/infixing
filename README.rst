Infixing
================================================================================

Introduction
--------------------------------------------------------------------------------

This library provides a easy way to defining many macros of infix-operator based syntax.
If you want a syntax sugar on Clojure, influenced from other languages, you can use the library as a selecting.
Maybe, this is usablitity for.


Examples
--------------------------------------------------------------------------------

.. code:: clojure

  (use 'infixing.core)

  ; First, Make a infix rule.
  ; The rule's priority is -1.
  ; The rule is non-associative.
  ; The "a :where b" will be replaced with "`(let ~b ~a)".
  (def where-rule (infix-map -1 :where (fn [a b] `(let ~b ~a))))

  ; Orders the list by where-rule.
  (infixing where-rule '(a :where b)) ; => (clojure.core/let b a)
  ; We don't replace all recursive. If list is got, it is S-Expression always.
  (infixing where-rule '((a :where b) :where c)) ; => (clojure.core/let c (a :where b))
  ; If the list is invalid syntax, we raise IllegalArgumentException together with human readable message.
  (infixing where-rule '(a :where b :where c)) ; => IllegalArgumentException Unexpected operator ``:where" in (a :where b ``:where" c)

  ; Next, define a macro.
  (defmacro do-where [& code]
    (infixing where-rule code))

  ; Now, you can use the macro!
  (do-where (println x) :where [ x "hello world" ])

Features
--------------------------------------------------------------------------------

Associativity
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**left-associative**

.. code:: clojure

  (def plus-rule (infixl 6 '+)) 
  (infixing plus-rule '(1 + 2 + 3)) ; => (+ (+ 1 2) 3)

**right-associative**

.. code:: clojure

  (def dollar-rule (infixr 0 '$)) 
  (infixing dollar-rule '(f $ g $ x)) ; => ($ f ($ g x))

**non-associative**

.. code:: clojure

  (def eq-rule (infix 4 '=))
  (infixing eq-rule '(a = b = c)) ; IllegalArgumentException Unexpected operator ``=" in (a = b ``=" c)

Priority
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: clojure

  (def plus-rule (infixl 6 '+)) 
  (def mult-rule (infixl 7 '*))
  (def plus-mult-rule (merge-rule plus-rule mult-rule))
  (infixing plus-mult-rule '(a * c + b * d)) ; => (+ (* a c) (* b d))

Mapping
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: clojure

  (def where-rule (infix-map -1 :where (fn [a b] `(let ~b ~a))))
  (infixing where-rule '(a :where b)) ; => (clojure.core/let b a)

Space as an infix-operator
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: clojure

  (def space-rule (infixl-space 10 (fn [f x] `(~f ~x))))
  (infixing space-rule '(f x y)) ; => ((f x) y)

Rule merging
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

you can merge rule by **merge-rule** function.
