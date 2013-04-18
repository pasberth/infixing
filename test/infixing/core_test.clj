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

(deftest add-rule-test1
  (testing "(x + y) == (+ x y)"
    (is (= (infixing add-rule '(x + y)) '(+ x y)))))
(deftest add-rule-test2
  (testing "(x + y + z) == (+ (+ x y) z)"
    (is (= (infixing add-rule '(x + y + z)) '(+ (+ x y) z)))))

(deftest add-mul-add-rule-test
  (testing "(a + b * c + d) == (+ (+ a (* b c)) d)"
    (is (= (infixing (rules add-rule mul-rule) '(a + b * c + d)) '(+ (+ a (* b c)) d)))))

(deftest mul-add-mul-rule-test
  (testing "(a * b + c * d) == (+ (* a b) (* c d))"
    (is (= (infixing (rules add-rule mul-rule) '(a * b + c * d)) '(+ (* a b) (* c d))))))

(deftest and-rule-test1
  (testing "(x and y) == (and x y)"
    (is (= (infixing and-rule '(x and y)) '(and x y)))))
(deftest and-rule-test2
  (testing "(x and y and z) == (and x (and y z))"
    (is (= (infixing and-rule '(x and y and z)) '(and x (and y z))))))

(deftest eq-and-eq-rule-test
  (testing "(a = b and c = d) == (and (= a b) (= c d))"
    (is (= (infixing (rules eq-rule and-rule) '(a = b and c = d)) '(and (= a b) (= c d))))))
