(ns clojure-studio.db
    (require [monger.core :as mg] [monger.collection :as mc]))

(def db (-> "mongodb://127.0.0.1/clojure_studio" mg/connect-via-url :db))
