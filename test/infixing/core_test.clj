(ns infixing.core-test
  (:use clojure.test
        infixing.core))

(def eq-rule    (infix  4 '=))
(def add-rule   (infixl 6 '+))
(def sub-rule   (infixl 6 '-))
(def mul-rule   (infixl 7 '*))
(def div-rule   (infixl 7 '/))
(def and-rule   (infixr 3 'and))
(def or-rule    (infixr 2 'or))
(def $-rule     (infixr 0 '$))
(def apply-rule (infixl-space 10 list))

(deftest ret-literal1
  (testing "x == x"
    (is (= (infixing add-rule 'x) 'x))))

(deftest ret-literal2
  (testing "(x) == (x)"
    (is (= (infixing add-rule '(x)) '(x)))))

(deftest add-rule-test1
  (testing "(x + y) == (+ x y)"
    (is (= (infixing add-rule '(x + y)) '(+ x y)))))
(deftest add-rule-test2
  (testing "(x + y + z) == (+ (+ x y) z)"
    (is (= (infixing add-rule '(x + y + z)) '(+ (+ x y) z)))))

(deftest add-mul-add-rule-test
  (testing "(a + b * c + d) == (+ (+ a (* b c)) d)"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a + b * c + d)) '(+ (+ a (* b c)) d)))))

(deftest mul-add-add-rule-test
  (testing "(a * b + c + d) == (+ (+ (* a b) c) d)"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a * b + c + d)) '(+ (+ (* a b) c) d)))))

(deftest add-add-mul-rule-test
  (testing "(a + b + c * d) == (+ (+ a b) (* c d))"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a + b + c * d)) '(+ (+ a b) (* c d))))))

(deftest mul-add-mul-rule-test
  (testing "(a * b + c * d) == (+ (* a b) (* c d))"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a * b + c * d)) '(+ (* a b) (* c d))))))

(deftest add-mul-mul-rule-test
  (testing "(a + b * c * d) == (+ a (* (* b c) d))"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a + b * c * d)) '(+ a (* (* b c) d))))))

(deftest mul-mul-add-rule-test
  (testing "(a * b * c + d) == (+ (* (* a b) c) d)"
    (is (= (infixing (merge-rule add-rule mul-rule) '(a * b * c + d)) '(+ (* (* a b) c) d)))))

(deftest and-rule-test1
  (testing "(x and y) == (and x y)"
    (is (= (infixing and-rule '(x and y)) '(and x y)))))
(deftest and-rule-test2
  (testing "(x and y and z) == (and x (and y z))"
    (is (= (infixing and-rule '(x and y and z)) '(and x (and y z))))))

(deftest eq-and-eq-rule-test
  (testing "(a = b and c = d) == (and (= a b) (= c d))"
    (is (= (infixing (merge-rule eq-rule and-rule) '(a = b and c = d)) '(and (= a b) (= c d))))))

(deftest eq-eq-rule-test
  (testing "(a = b = c) == IllegalArgumentException"
    (is (thrown? IllegalArgumentException (infixing eq-rule '(a = b = c))))))

(deftest or-and-or-rule-test
  (testing "(a or b and c or d) == (or a (or (and b c) d))"
    (is (= (infixing (merge-rule or-rule and-rule) '(a or b and c or d)) '(or a (or (and b c) d))))))

(deftest and-or-or-rule-test
  (testing "(a and b or c or d) == (or (and a b) (or c d))"
    (is (= (infixing (merge-rule or-rule and-rule) '(a and b or c or d)) '(or (and a b) (or c d))))))

(deftest or-or-and-rule-test
  (testing "(a or b or c and d) == (or a (or b (and c d)))"
    (is (= (infixing (merge-rule or-rule and-rule) '(a or b or c and d)) '(or a (or b (and c d)))))))

(deftest and-or-and-rule-test
  (testing "(a and b or c and d) == (or (and a b) (and c d))"
    (is (= (infixing (merge-rule or-rule and-rule) '(a and b or c and d)) '(or (and a b) (and c d))))))

(deftest or-and-and-rule-test
  (testing "(a or b and c and d) == (or a (and b (and c d)))"
    (is (= (infixing (merge-rule or-rule and-rule) '(a or b and c and d)) '(or a (and b (and c d)))))))

(deftest and-and-or-rule-test
  (testing "(a and b and c or d) == (or (and a (and b c)) d)"
    (is (= (infixing (merge-rule or-rule and-rule) '(a and b and c or d)) '(or (and a (and b c)) d)))))

(deftest space-rule-test
  (testing "(a b $ x y) == IllegalArgumentException"
    (is (thrown? IllegalArgumentException (infixing $-rule '(a b $ x y)) '($ a b x y)))))

(deftest apply-rule-test
  (testing "(f a b $ g x y) == ($ ((f a) b) ((g x) y))"
    (is (= (infixing (merge-rule apply-rule $-rule) '(f a b $ g x y)) '($ ((f a) b) ((g x) y))))))

