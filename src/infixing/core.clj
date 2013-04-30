(ns infixing.core)

(defn infixing [rules code]
  (loop [  stack '()
           code  code
        ]
    (let [ [ left-rule left-node & stack ] stack ]
    (letfn [ (ret- [a]
               (let [ nodes     (map second (partition 2 stack))
                      last-node (concat a left-node)
                    ]
                 (reduce (fn [a b] `(~@b ~a)) (map reverse (cons last-node nodes)))))
             (ret  [] (ret- code))
             (ret1 [] (ret- `(~code)))
           ] (cond
      (not (seq? code))  (ret1)
      (empty? code)      (ret)
      (= 1 (count code)) (ret)
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
          :else                                          'undefined)))))))

(defn infix [priority symbol & symbols]
  (let [symbols (cons symbol symbols)]
    (reduce merge (map (fn [s] {s {:priority priority :recur nil}}) symbols))))

(defn infixl [priority symbol & symbols]
  (let [symbols (cons symbol symbols)]
    (reduce merge (map (fn [s] {s {:priority priority :recur :left}}) symbols))))

(defn infixr [priority symbol & symbols]
  (let [symbols (cons symbol symbols)]
    (reduce merge (map (fn [s] {s {:priority priority :recur :right}}) symbols))))

(defn rules [& rules]
  (reduce merge rules))
