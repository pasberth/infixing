(ns infixing.core)

(defn infixing [rules code]
  (let   [ nodes  #(map second (partition 2 %))
           ret    #(reduce (fn [a b] `(~@b ~a)) %)
           return #(ret (cons (concat %2 %1) (nodes %3)))
         ]
  (loop [ [ left-rule left-node & stack ] '()
          code  code
        ] (cond
    (not (seq? code))  (return `(~code) left-node stack)
    (> 2 (count code)) (return code left-node stack)
    :else
      (let [ [ lft op & code ] code
             op-rule           (rules op)
             op-pr             (op-rule :priority)
             op-rc             (op-rule :recur)
             l-pr              (and left-rule (left-rule :priority))
             l-rc              (and left-rule (left-rule :recur))
           ] (cond
        (nil? op-rule)           (recur `(~left-rule (~@left-node ~lft) ~@stack) (cons op code))
        (nil? left-rule)         (recur `(~op-rule (~op ~@left-node ~lft) ~@stack) code)
        (< op-pr l-pr)           (recur stack `((~@left-node ~lft) ~op ~@code))
        (or (> op-pr l-pr)
          (= :right op-rc l-rc)) (recur `(~op-rule (~op ~lft) ~left-rule ~left-node ~@stack) code)
        (= :left  op-rc l-rc)    (recur `(~op-rule (~op (~@left-node ~lft)) ~@stack) code)
        :else                 'undefined))))))

(defn infix [priority symbol]
  {symbol {:priority priority :recur nil}})

(defn infixl [priority symbol]
  {symbol {:priority priority :recur :left}})

(defn infixr [priority symbol]
  {symbol {:priority priority :recur :right}})

(defn rules [& rules]
  (reduce merge rules))
