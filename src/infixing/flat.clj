(ns infixing.flat
  (:use infixing.core))

(def flatr-rule (infixr-map 0 '$ #(concat %1 (list %2))))
(def flat-rule flatr-rule)

(defn infixing-with-flat [code]
  (infixing flat-rule code))

(defmacro with-flat [& code]
  (infixing-with-flat code))