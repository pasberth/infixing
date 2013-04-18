(ns infixing.control.prelude
  (:require [infixing.core :as core]))

(defn $ [f x]
  (f x))

(defmacro where [a b]
  `(let ~b ~a))

(def $-rule     (core/infixr 0 '$))
(def where-rule (core/infix -1 'where))
(def ->-rule    (core/infixl 1 '->))
