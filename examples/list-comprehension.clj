(use 'infixing.core)

(def <-rule (infix-map 9 :< (fn [v l]
                                (fn [f] (f v l)))))
(def when-rule (infixl-map 5 :when (fn [w x]
                                 (w (fn [v l]
                                   (let [l `(filter (fn [~v] ~x) ~l)]
                                     (fn [f] (f v l))))))))
(def space-rule (infixl-space 5 (fn [w w-]
                                  (w (fn [v l]
                                    (w- (fn [v- l-]
                                      (fn [f] (f (vector v v-) `(for [~v ~l ~v- ~l-] (list ~v ~v-)))))))))))

(def list-comp-rule (rules <-rule when-rule space-rule))
(defmacro list-comp [[x & w]]
  ((infixing list-comp-rule w) (fn [v l] `(map (fn [~v] ~x) ~l))))

(println (list-comp (x, x :< '(1 2 3))))
; => (1 2 3)
(println (list-comp ((* x x), x :< (range 1 10) :when (even? x))))
; => (4 16 36 64)
(println (list-comp ((list x y), x :< '(1 2 3)
                                 y :< '(1 2))))
; => ((1 1) (1 2) (2 1) (2 2) (3 1) (3 2))
(println (list-comp ((list x y), x :< '(1 2 3)
                                 y :< (range (inc x) 5))))
; => ((1 2) (1 3) (1 4) (2 3) (2 4) (3 4))
(println (list-comp ((list x y), x :< '(1 2 3)
                                 y :< (range (inc x) 5))))
; => ((1 2) (1 3) (1 4) (2 3) (2 4) (3 4))
(println (list-comp ((list x y), x :< (range 1 5)       :when (even? x)
                                 y :< (range (inc x) 5) :when (odd? y))))
; => ((2 3))