(ns infixing.control.prelude
  (:use [infixing.core]))

(defn $ [f x]
  (f x))

(defmacro where [a b]
  `(let ~b ~a))

(def $-rule     (infixr 0 '$))
(def where-rule (infix -1 'where))
(def ->-rule    (infixl 1 '->))
(def eq-rule    (infix 4 '=))
(def add-rule   (infixl 6 '+))
(def sub-rule   (infixl 6 '-))
(def mul-rule   (infixl 7 '*))
(def div-rule   (infixl 7 '/))
(def and-rule   (infixr 3 'and))
(def or-rule    (infixr 2 'or))

(def rule (rules $-rule
                 where-rule
                 ->-rule
                 eq-rule
                 add-rule
                 sub-rule
                 mul-rule
                 div-rule
                 and-rule
                 or-rule))
