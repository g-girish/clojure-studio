(ns clojure-studio.core-test
  (:require [clojure.test :refer :all]
            [clojure-studio.core :refer :all]
            [resource-mgr.restaurant :refer [getMenu]]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest get-items-from-menu
  (testing "test getMenu function")
  (let [items (getMenu)]
    (is (not-empty items))))
