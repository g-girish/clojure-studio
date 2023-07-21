(ns clojure-studio.restaurant
    (:require [clojure.data.json :as json]
              [clojure.string :as str]
              [clojure-studio.db :as db]
              [clojure.java.jdbc :as j]))

(defn getMenu []
    (j/query db/pg-db ["select * from gl_menu"]))

(defn menu [req]
    {:status 200
    :headers {"Content-Type" "text/json"}
    :body (json/write-str (getMenu))})