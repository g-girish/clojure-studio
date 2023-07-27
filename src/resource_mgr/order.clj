(ns resource-mgr.order
    (:require [resource-mgr.customer :as customer]
              [clojure.data.json :as json]
              [clojure.string :as str]))

(defn createOrder [params customer] )
;      (let [item_id (:item-id params) customer_id (:id customer)]
;        (j/insert! db/pg-db :gl_orders {:customer_id customer_id :item_id item_id})))

(defn order [req]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (let [customer (customer/createCustomer (:params req))]
           (if (empty? customer)
             (do
               (println "Failed to create customer"))
             (do
               (let [add_order (createOrder (:params req) (first customer))]
                 add_order)
               (println "Customer created successfully with id" (:id (first customer)))))
           customer)})