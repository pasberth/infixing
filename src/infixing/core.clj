(ns infixing.core)

(defrecord Rules [space-rule rule-map])
(defrecord Rule  [priority right-recursion? left-recursion? node-map])

(defn infixing [rules code]
  (if (not (seq? code)) code
  (let   [ ret         #(reduce (fn [a [b-rule b]] ((:node-map b-rule) `(~@b ~a))) %)
           return      #(ret (cons ((:node-map %2) %1) (partition 2 %3)))
           spc-op      (fn [] true)
           throw-error (fn [idx] (if (even? idx)
                         (let [n (/ idx 2) op (nth code n) top (take n code) btm (drop (inc n) code)]
                           (throw (IllegalArgumentException. (format "Unexpected operator ``%s\" in %s" op `(~@top ~(symbol (format "``%s\"" op))  ~@btm)))))
                         (let [next-n (/ (inc idx) 2) prev-n (dec next-n)
                               next-op (nth code next-n) prev-op (nth code prev-n)
                               top (take prev-n code) btm (drop (inc next-n) code)]
                           (throw (IllegalArgumentException. (format "Unexpected space between ``%s\" and ``%s\" in %s" prev-op next-op `(~@top ~(symbol (format "``%s %s\"" prev-op next-op))  ~@btm)))))))
         ]
  (loop [ [ left-rule left-node & stack ] '()
          code                            code
          idx                             2
        ] (cond
    (> 2 (count code)) (cond (nil? left-node) code :else (return (concat left-node code) left-rule stack))
    :else
      (let [ [ lft op & code ] code
             op-rule           (if (= spc-op op) (:space-rule rules) ((:rule-map rules) op))
             op-pr             (and op-rule (:priority op-rule))
             op-r?             (and op-rule (:right-recursion? op-rule))
             op-l?             (and op-rule (:left-recursion? op-rule))
             l-pr              (and left-rule (:priority left-rule))
             l-r?              (and left-rule (:right-recursion? left-rule))
             l-l?              (and left-rule (:left-recursion? left-rule))
           ] (cond
        (nil? op-rule)       (cond
          (:space-rule rules)  (recur `(~left-rule ~left-node ~@stack) `(~lft ~spc-op ~op ~@code) (- idx 1))
          :else                (throw-error (- idx 1)))
        (nil? left-rule)     (recur `(~op-rule (~op ~@left-node ~lft) ~@stack) code (+ 4 idx))
        (< op-pr l-pr)       (recur stack `(~((:node-map left-rule) `(~@left-node ~lft)) ~op ~@code) idx)
        (or (> op-pr l-pr)
          (and op-r? l-r?))  (recur `(~op-rule (~op ~lft) ~left-rule ~left-node ~@stack) code (+ 4 idx))
        (and op-l? l-l?)     (recur `(~op-rule (~op ~((:node-map left-rule) `(~@left-node ~lft))) ~@stack) code (+ 4 idx))
        :else                (throw-error idx))))))))

(defn infix [priority symbol]
  (Rules. nil {symbol (Rule. priority false false identity)}))

(defn infixl [priority symbol]
  (Rules. nil {symbol (Rule. priority false true identity)}))

(defn infixr [priority symbol]
  (Rules. nil {symbol (Rule. priority true false identity)}))

(defn infix-map [priority symbol node-map]
  (Rules. nil {symbol (Rule. priority false false (fn [[_ a b]] (node-map a b)))}))

(defn infixl-map [priority symbol node-map]
  (Rules. nil {symbol (Rule. priority false true (fn [[_ a b]] (node-map a b)))}))

(defn infixr-map [priority symbol node-map]
  (Rules. nil {symbol (Rule. priority true false (fn [[_ a b]] (node-map a b)))}))

(defn infix-space [priority node-map]
  (Rules. (Rule. priority false false (fn [[_ a b]] (node-map a b))) {}))

(defn infixl-space [priority node-map]
  (Rules. (Rule. priority false true (fn [[_ a b]] (node-map a b))) {}))

(defn infixr-space [priority node-map]
  (Rules. (Rule. priority true false (fn [[_ a b]] (node-map a b))) {}))

(defn merge-rule [& rules]
  (Rules. (reduce (fn [a b] (or b a)) (map :space-rule rules)) (reduce merge (map :rule-map rules))))
