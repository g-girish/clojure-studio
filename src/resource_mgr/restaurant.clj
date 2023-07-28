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

(defn menu [req]
    {:status 200
    :headers {"Content-Type" "text/json"}
    :body (json/write-str (getMenu))})