(defn $ [f x]
  (f x))

(defmacro where [a b]
  `(let ~b ~a))

(def $-rule     (infixr 0 '$))
(def where-rule (infix -1 'where))
(def ->-rule    (infixl 1 '->))
