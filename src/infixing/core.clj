(defn infixing-infix [rules infix-rule [a b & code]] (cond
  (nil? b)         `(~a)
  (nil? (rules b)) (cond
    (seq? a) (infixing-infix rules infix-rule `((~@a ~b) ~@code))
    :else    (infixing-infix rules infix-rule `((~a ~b) ~@code)))
  :else            (let [ b-rule (rules b) ] (cond
    (< (b-rule :priority) (infix-rule :priority))  `(~a (~b ~@code))
    (> (b-rule :priority) (infix-rule :priority))  (infixing-infix rules infix-rule `((~b ~a) ~@code))
    (= :left (b-rule :recur) (infix-rule :recur))  `(~a (~b ~@code))
    (= :right (b-rule :recur) (infix-rule :recur)) (infixing-infix rules infix-rule `((~b ~a) ~@code))
    :else                                          'undefined))))

(declare infixing infixing-recur)

(defn infixing-entry [rules [left curr & code]] (cond
  (nil? curr)         left
  (nil? (rules curr)) (cond
    (seq? left) (infixing-entry rules `((~@left ~(infixing-recur rules curr)) ~@code))
    :else       (infixing-entry rules `((~left ~(infixing-recur rules curr)) ~@code)))
  :else               (let [ [right code] (infixing-infix rules (rules curr) code) ] (cond
    (empty? code)       `(~(infixing-recur rules curr) ~left ~(infixing-recur rules right))
    :else               (infixing-entry rules `((~(infixing-recur rules curr) ~left ~(infixing-recur rules right)) ~@code))))))

(defn infixing-recur [rules code] (cond
  (seq? code) (let [[left curr & code] code] (cond
    (seq? curr) (infixing rules `(~left ~(infixing-recur rules curr) ~@code))
    :else       (infixing rules `(~left ~curr ~@code))))
  :else       code))

(defn infixing [rules [left curr & code]]
  (infixing-entry rules `(~(infixing-recur rules left) ~curr ~@code)))

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
