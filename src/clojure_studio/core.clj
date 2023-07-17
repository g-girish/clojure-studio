(ns clojure-studio.core
    (:require [org.httpkit.server :as server]
              [compojure.core :refer :all]
              [compojure.route :as route]
              [ring.middleware.defaults :refer :all]
              [clojure.pprint :as pp]
              [clojure.string :as str]
              [clojure.data.json :as json])
    (:gen-class))

(def employee-collection (atom []))

(defn addEmployee "add employee to collection" [fname lname]
    (swap! employee-collection conj {:firstname (str/capitalize fname) :lastname (str/capitalize lname)}))

(defn removeEmployees "delete all employees" []
    (reset! employee-collection {}))

(defn employee-handler "list all employees handler" [req]
    {:status 200
     :headers {"Content-Type"  "text/plain"}
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

(defroutes api-routes
    (GET "/employees" [] employee-handler)
    (GET "/employees/remove" [] remove-employees-handler)
    (POST "/employee/add" [] add-employee-handler)
    (route/not-found "404 - Page not found"))

(defn -main
  "First entry"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'api-routes (assoc-in site-defaults [:security :anti-forgery] false)) {:port port})
    ; Run the server without ring defaults
    ; (server/run-server #'api-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
