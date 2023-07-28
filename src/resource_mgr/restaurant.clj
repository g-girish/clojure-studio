(ns resource-mgr.restaurant
    (:require [clojure.data.json :as json]
              [clojure.string :as str]
              [clojure-studio.db :refer [pg-db]]
              [clojure.java.jdbc :as j]))

(defn getMenu []
  (let [result (j/query pg-db ["SELECT * FROM gl_menu"])]
    result))

;(defn getItems [itemIds]
;  (let [sql-query (str "SELECT * FROM gl_menu WHERE id IN ("
;                       (str/join ", " (map str itemIds))
;                       ")")]
;    (j/query pg-db [sql-query])))

(defn requestBill [params]
  (let [order-id    (:order-id params)
        customer-id (:customer-id params)
        data        (j/query pg-db
                             ["SELECT o.id as order_id, json_agg(json_build_object('item_id', i.id, 'name', i.name, 'price', i.price, 'quantity', m.qty)) AS items, o.subtotal::bigint, o.tax::smallint, o.created_at, o.status, o.total::bigint FROM gl_orders o JOIN gl_order_items_map m ON o.id = m.order_id JOIN gl_menu i ON m.item_id = i.id where o.id = ? and o.customer_id = ? GROUP BY o.id, o.created_at, o.customer_id"
                              order-id
                              customer-id])]
    data))

(defn menu [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (json/write-str (getMenu))})

(defn request [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (requestBill (:params req))})