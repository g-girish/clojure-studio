(ns clojure-studio.core
    (:require [compojure.core :refer :all]
              [clojure.pprint :as pp]
              [clojure.string :as str]
              [clojure.data.json :as json]
              [clojure-studio.db :as db])
    (:gen-class))

(def employee-collection (atom []))

(defn addEmployee "add employee to collection" [fname lname]
    (swap! employee-collection conj {:firstname (str/capitalize fname) :lastname (str/capitalize lname)}))

(defn removeEmployees "delete all employees" []
    (reset! employee-collection {}))

(defn employee-handler "list all employees handler" [req]
    {:status 200
     :headers {"Content-Type"  "text/plain"}
;;      :body (json/write-str (db/all-employees))})
     :body  (str (json/write-str @employee-collection))})

(defn add-employee-handler "add an employee handler" [req]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (let [fname (:fname (:params req)) lname (:lname (:params req))]
        (str (json/write-str (addEmployee fname lname))))})

(defn remove-employees-handler "remove all employees handler" [req]
    {:status 204
     :headers {"Content-Type" "text/plain"}
     :body (-> (removeEmployees) (str "Deleted"))})

