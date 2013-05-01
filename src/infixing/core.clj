(ns infixing.core)

(defrecord Rules [space-rule rule-map])
(defrecord Rule  [priority right-recursion? left-recursion? node-map])

(defn infixing [rules code]
  (let   [ ret    #(reduce (fn [a [b-rule b]] ((:node-map b-rule) `(~@b ~a))) %)
           return #(ret (cons ((:node-map %2) (concat %3 %1)) (partition 2 %4)))
           spc-op (fn [] true)
         ]
  (loop [ [ left-rule left-node & stack ] '()
          code                            code
        ] (cond
    (not (seq? code))  (return `(~code) left-rule left-node stack)
    (> 2 (count code)) (return code left-rule left-node stack)
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
          (:space-rule rules) (recur `(~left-rule ~left-node ~@stack) `(~lft ~spc-op ~op ~@code))
          :else              (recur `(~left-rule (~@left-node ~lft) ~@stack) (cons op code)))
        (nil? left-rule)     (recur `(~op-rule (~op ~@left-node ~lft) ~@stack) code)
        (< op-pr l-pr)       (recur stack `(~((:node-map left-rule) `(~@left-node ~lft)) ~op ~@code))
        (or (> op-pr l-pr)
          (and op-r? l-r?))  (recur `(~op-rule (~op ~lft) ~left-rule ~left-node ~@stack) code)
        (and op-l? l-l?)     (recur `(~op-rule (~op ~((:node-map left-rule) `(~@left-node ~lft))) ~@stack) code)
        :else                'undefined))))))

(defn infix [priority symbol]
  (Rules. nil {symbol (Rule. priority false false identity)}))

(defn infixl [priority symbol]
  (Rules. nil {symbol (Rule. priority false true identity)}))

(defn infixr [priority symbol]
  (Rules. nil {symbol (Rule. priority true false identity)}))

(defn rules [& rules]
  (Rules. nil (reduce merge (map :rule-map rules))))
