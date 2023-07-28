(ns resource-mgr.order
    (:require [resource-mgr.customer :as customer]
              [clojure.data.json :as json]
              [clojure.string :as str]
              [clojure.java.jdbc :as j]
              [clojure-studio.db :refer [pg-db]]))

(defn execute-transaction [db-spec operations]
  (j/with-db-transaction [pg-db db-spec]
                         (doseq [op operations]
                           (j/execute! pg-db op))))

(defn getTaxAmount [total] (* 0.05 total))

(defn getItemsTotal [itemIds]
  (let [sql-query (str "SELECT SUM(price)::bigint as total FROM gl_menu WHERE id IN ("
                       (str/join ", " (map str itemIds))
                       ")")
        result    (j/query pg-db [sql-query])]
    (->> result (first) (:total))))

(defn prepareOrderItemMap [orderId itemsIds]
  (try
    (j/with-db-transaction [pg-db pg-db]
                           (doseq [item itemsIds]
                             (j/execute! pg-db
                                         ["INSERT INTO gl_order_items_map (item_id, order_id, qty) VALUES (?, ?, ?)"
                                          item
                                          orderId
                                          1])))
    (catch java.sql.SQLException _
      (str "An SQL error occured"))
    (catch Exception e
      (str "An error occured: " (.getMessage e)))))

(defn createOrder [params customer]
  (try
    (let [sub-total          (getItemsTotal (:item-id params))
          tax                (getTaxAmount sub-total)
          total              (+ sub-total tax)]
      (let [order (j/insert! pg-db :gl_orders
                             {:customer_id (:id customer)
                              :subtotal    sub-total
                              :tax         tax
                              :total       total
                              :status      "placed"})]
        (prepareOrderItemMap (:id (first order)) (:item-id params))
        order))
    (catch java.sql.SQLException _
      (str "An SQL error occured"))
    (catch Exception e
      (str "An error occured: " (.getMessage e)))))

(defn getOrder [params]
  (if (:id params)
    (do
      (let [order-id (:id params)]
        (j/query pg-db
                 ["SELECT o.id as order_id, o.customer_id, json_agg(json_build_object('item_id', i.id, 'name', i.name, 'price', i.price, 'quantity', m.qty)) AS items, o.subtotal::smallint, o.tax::smallint, o.created_at, o.status, o.total::bigint, o.payment_mode FROM gl_orders o JOIN gl_order_items_map m ON o.id = m.order_id JOIN gl_menu i ON m.item_id = i.id where o.id = ? GROUP BY o.id, o.created_at, o.customer_id"
                  order-id])))
    (do
      (str "id param is required"))))

(defn updateOrderStatus [params]
  (try
    (let [updateQuery "UPDATE gl_orders SET status = ? WHERE id = ?"
          status      (:status params)
          order-id    (:order-id params)
          execute     (j/execute! pg-db [updateQuery status order-id])]
      (if execute
        (do
          (str "Status updated."))
        (do
          (str "Status not updated."))))
    (catch java.sql.SQLException _
      (str "An SQL error occured"))
    (catch Exception e
      (str "An error occured:" (.getMessage e)))))

(defn payBill [params]
  (try
    (let [updateQuery       "UPDATE gl_orders SET status = 'complete', payment_mode = ? WHERE id = ?"
          payment-mode      (:payment-mode params)
          order-id          (:order-id params)
          execute           (j/execute! pg-db [updateQuery payment-mode order-id])]
      (if execute
        (do
          (str "Bill Paid."))
        (do
          (str "Bill not paid."))))
    (catch java.sql.SQLException _
      (str "An SQL error occured"))
    (catch Exception e
      (str "An error occured:" (.getMessage e)))))

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

(defn details [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (getOrder (:params req))})

(defn update [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (updateOrderStatus (:params req))})

(defn pay [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (payBill (:params req))})

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

;(defn prepareOrderItemMap [orderId itemsIds]
;  (def items (map #(hash-map :item_id % :order_id orderId :qty 1) itemsIds))
;  (let [result (remove-brackets-and-commas (str items))]
;    (println result)
;    (j/insert-multi! pg-db :gl_order_items_map [result])
;    ))


;(defn prepareOrder [params customer]
;  (let [insert-query "INSERT INTO gl_orders (customer_id, subtotal, tax, total, status) VALUES (?, ?, ?, ?, ?::order_statuses)"
;        sub-total (getItemsTotal (:item-id params))
;        tax (getTaxAmount sub-total)
;        total (+ sub-total tax)]
;    (let [order (execute-transaction pg-db
;                                     [(vector insert-query (:id customer) sub-total tax total "placed")])]
;      (println order))))