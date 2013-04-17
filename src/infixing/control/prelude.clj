(defn $ [f x]
  (f x))

(defmacro where [a b]
  `(let ~b ~a))

(def $-rule     (infix 0  '$))
(def where-rule (infix -1 'where))
