(ns clojure-studio.web
    (:require [compojure.core :refer :all]
              [org.httpkit.server :as server]
              [clojure.string :as str]
              [ring.middleware.defaults :refer :all]
              [clojure-studio.core :as studio]
              [clojure-studio.db :as db]
              [ring.util.response :as resp]
              [compojure.route :as route]
              [clojure-studio.restaurant :as res])
     (:gen-class))

(defroutes api-routes
    (GET "/employees" [] studio/employee-handler)
    (GET "/employees/remove" [] studio/remove-employees-handler)
    (POST "/employee/add" [] studio/add-employee-handler)
;;     (POST "/articles" [title body]
;;       (do (db/create-article title body)
;;          (resp/redirect "/")))
    (GET "/menu" [] res/menu)

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

