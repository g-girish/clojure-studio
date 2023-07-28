(ns clojure-studio.db
    (:require [clojure.java.jdbc :as j])
    (:gen-class))

(def pg-db {:dbtype "postgresql"
            :dbname "clojure_studio"
            :host "localhost"
            :user "girish"
            :password "password"
            :stringtype "unspecified"})

(defn all-employees []
  (j/query pg-db ["select * from employees"]))