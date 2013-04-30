(ns infixing.core)

(defn infixing [rules code]
  (let   [ nodes #(map second (partition 2 %))
           ret   #(reduce (fn [a b] `(~@b ~a)) (map reverse %))
         ]
  (letfn [ (return [code [_ left-node & stack]]
             (ret (cons (concat code left-node) (nodes stack))))
         ]
  (loop [ stack '()
          code  code
        ]
  (let [ [ left-rule left-node & stack ] stack ]
  (cond
    (not (seq? code))  (return `(~code) `(~left-rule ~left-node ~@stack))
    (empty? code)      (return code `(~left-rule ~left-node ~@stack))
    (= 1 (count code)) (return code `(~left-rule ~left-node ~@stack))
    :else
      (let [ [ lft op & code ] code
             op-rule           (rules op)
           ] (cond
        (nil? op-rule)                                 (recur `(~left-rule ~(cons lft left-node) ~@stack) (cons op code))
        (nil? left-rule)                               (recur `(~op-rule (~lft ~@left-node ~op) ~@stack) code)
        (< (op-rule :priority) (left-rule :priority))  (recur stack `((~@(reverse left-node) ~lft) ~op ~@code))
        (> (op-rule :priority) (left-rule :priority))  (recur `(~op-rule (~lft ~op) ~left-rule ~left-node ~@stack) code)
        (= :right (op-rule :recur) (left-rule :recur)) (recur `(~op-rule (~lft ~op) ~left-rule ~left-node ~@stack) code)
        (= :left (op-rule :recur) (left-rule :recur))  (recur `(~op-rule ((~@(reverse left-node) ~lft) ~op) ~@stack) code)
        :else                                          'undefined))))))))

(defn infix [priority symbol]
  {symbol {:priority priority :recur nil}})

(defn infixl [priority symbol]
  {symbol {:priority priority :recur :left}})

(defn infixr [priority symbol & symbols]
  {symbol {:priority priority :recur :right}})

(defn rules [& rules]
  (reduce merge rules))
