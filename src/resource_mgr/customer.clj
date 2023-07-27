(ns resource-mgr.customer
    (:require [clojure.data.json :as json]
              [clojure.java.jdbc :as j]
              [clojure-studio.db :as db]
              [clojure.string :as str]))

(defn createCustomer [params]
  (let [name (:name params) phone (:phone params)]
    (j/insert! db/pg-db :gl_customers {:name name :phone phone})))