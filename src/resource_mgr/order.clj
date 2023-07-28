(ns resource-mgr.order
    (:require [resource-mgr.customer :as customer]
              [clojure.data.json :as json]
              [clojure.string :as str]
              [clojure.java.jdbc :as j]
              [clojure-studio.db :refer [pg-db]]))

;(defn execute-transaction [db-spec operations]
;  (j/with-db-transaction [pg-db db-spec]
;                         (doseq [op operations]
;                           (println (j/execute! pg-db op)))))

(defn getTaxAmount [total]
  (* 0.05 total))

(defn getItemsTotal [itemIds]
  (let [sql-query (str "SELECT SUM(price)::bigint as total FROM gl_menu WHERE id IN ("
                       (str/join ", " (map str itemIds))
                       ")")
        result    (j/query pg-db [sql-query])]
    (->> result
         (first)
         (:total))))

;(defn prepareOrder [params customer]
;  (let [insert-query "INSERT INTO gl_orders (customer_id, subtotal, tax, total, status) VALUES (?, ?, ?, ?, ?::order_statuses)"
;        sub-total (getItemsTotal (:item-id params))
;        tax (getTaxAmount sub-total)
;        total (+ sub-total tax)]
;    (let [order (execute-transaction pg-db
;                                     [(vector insert-query (:id customer) sub-total tax total "placed")])]
;      (println order))))

(defn prepareOrderItemMap [orderId itemsIds]
  (j/with-db-transaction [pg-db pg-db]
                         (doseq [item itemsIds]
                           (j/execute! pg-db
                                       ["INSERT INTO gl_order_items_map (item_id, order_id, qty) VALUES (?, ?, ?)"
                                        item
                                        orderId
                                        1]))))

;(defn prepareOrderItemMap [orderId itemsIds]
;  (def items (map #(hash-map :item_id % :order_id orderId :qty 1) itemsIds))
;  (let [result (remove-brackets-and-commas (str items))]
;    (println result)
;    (j/insert-multi! pg-db :gl_order_items_map [result])
;    ))

(defn createOrder [params customer]
  (let [sub-total          (getItemsTotal (:item-id params))
        tax                (getTaxAmount sub-total)
        total              (+ sub-total tax)]
    ;    (execute-transaction pg-db [(vector insert-query (:id customer) sub-total tax total "placed")])
    (let [order (j/insert! pg-db :gl_orders
                           {:customer_id (:id customer)
                            :subtotal    sub-total
                            :tax         tax
                            :total       total
                            :status      "placed"})]
      (prepareOrderItemMap (:id (first order)) (:item-id params))
      order)))

;(defn prepareOrder [params customer]
;  (let [insert-query       "INSERT INTO gl_orders (customer_id, subtotal, tax, total, status) VALUES (?, ?, ?, ?, ?::order_statuses)"
;        sub-total          (getItemsTotal (:item-id params))
;        tax                (getTaxAmount sub-total)
;        total              (+ sub-total tax)
;        set-deferred-query "SET CONSTRAINTS ALL DEFERRED"]
;    (j/with-db-transaction [pg-db pg-db]
;                           (j/execute! pg-db set-deferred-query)
;                           (execute-transaction pg-db [(vector insert-query (:id customer) sub-total tax total "placed")])
;                           (let [order-id (j/query
;                                           pg-db
;                                           ["SELECT lastval() AS order_id"])
;                                 order-id (:order_id (first order-id))]
;                             (println "last id" order-id)
;                             (prepareOrderItemMap order-id (:item-id params))))))

;                                 (let [order (j/insert! pg-db :gl_orders
;                                                        {:customer_id (:id customer)
;                                                         :subtotal    sub-total
;                                                         :tax         tax
;                                                         :total       total
;                                                         :status      "placed"})]
;                                   (prepareOrderItemMap (:id (first order)) (:item-id params))))))

;(j/execute! pg-db [insert-query (:id customer) sub-total tax total "placed"])

;      (let [item_id (:item-id params) customer_id (:id customer)]
;        (j/insert! db/pg-db :gl_orders {:customer_id customer_id :item_id item_id})))

(defn getOrder [params]
  (if (:id params)
    (do
      (let [order-id (:id params)]
        (j/query pg-db
                 ["SELECT o.id, o.created_at, o.customer_id, m.id, m.name, m.price::bigint, o.status FROM gl_orders o JOIN gl_order_items_map oim ON o.id = oim.order_id JOIN gl_menu m ON oim.item_id = m.id where o.id = ?"
                  order-id])))
    (do
      (str "id param is required"))))

(defn order [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (let [customer (customer/createCustomer (:params req))]
              (println (:params req))
              (if (empty? customer)
                (do
                  (println "Failed to create customer"))
                (do
                  (let [add_order (createOrder (:params req) (first customer))]
                    (println add_order))
                  (println "Customer created successfully with id" (:id (first customer)))))
              customer)})

(defn prepareOrder [params]
  (println params)
  (let [status   "preparing"
        order-id (:order-id params)]
    (j/with-db-transaction [pg-db pg-db]
                           (j/execute! pg-db
                                       ["UPDATE gl_orders SET status = ? WHERE id = ?"
                                       status order-id]))))

(defn details [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (getOrder (:params req))})

(defn prepare [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (prepareOrder (:params req))})
