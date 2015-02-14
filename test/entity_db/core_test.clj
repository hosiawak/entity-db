(ns entity-db.core-test
  (:require [clojure.test :refer :all]
            [entity-db.core :as core]))

(defmacro with-db
  "Creates dbname, evaluates body, deletes dbname"
  [dbname & body]
  (list 'do `(core/db-create ~dbname) (first body) `(core/db-delete ~dbname)))

(deftest db-list
  (testing "is empty by default"
    (is (= [] (core/db-list))))
  (testing "returns a list of dbs"
    (with-db "test"
      (is (= ["test"] (core/db-list))))))

(deftest db-create
  (testing "creates the db and returns true if created"
    (do
      (is (= true (core/db-create "test")))
      (core/db-delete "test")))
  (testing "returns false if not created"
    (with-db "test"
      (is (= false (core/db-create "test"))))))

(deftest db-delete
  (testing "deletes the db and returns true if deleted"
    (with-db "test"
      (is (= true (core/db-delete "test")))))
  (testing "returns false if not deleted"
    (is (= false (core/db-delete "test")))))

(deftest entity-type-list
  (testing "returns an empty list by default"
    (with-db "test"
      (is (= [] (core/entity-type-list "test")))))
  (testing "returns a list of unique entity types sorted by name"
    (with-db "test"
      (do
        (core/attr-create "test" "study" {:name "name" :description "Study name"})
        (core/attr-create "test" "xavier" {:name "name" :description "Xavier name"})
        (core/attr-create "test" "customer" {:name "name" :description "Customer name"})
        (is (= ["customer" "study" "xavier"] (core/entity-type-list "test")))))))

(deftest attr-create
  (testing "creates a string attribute for an entity with a description"
    (with-db "test"
      (do
        (core/attr-create "test" "customer" {:name "name" :description "Customer name"})
        (is (some #{:customer/name} (map #(:name %) (core/attr-list "test" "customer")))))))
  (testing "creates a long attribute"
    (with-db "test"
      (do
        (core/attr-create "test" "customer" {:name "age" :type "long" :description "Customer age"})
        (is (some #{:customer/age} (map #(:name %) (core/attr-list "test" "customer")))))))
  (testing "creates an instant attribute"
    (with-db "test"
      (do
        (core/attr-create "test" "customer" {:name "dob" :type "instant" :description "Customer date of birth"})
        (is (some #{:customer/dob} (map #(:name %) (core/attr-list "test" "customer")))))))
    (testing "creates an reference attribute"
    (with-db "test"
      (do
        (core/attr-create "test" "customer" {:name "emails" :type "ref" :description "Customer's emails"})
        (is (some #{:customer/emails} (map #(:name %) (core/attr-list "test" "customer"))))))))

(deftest entity-create-and-list
  (testing "creates an entity record"
    (with-db "test"
      (do
        (core/attr-create "test" "study" {:name "name" :description "Customer name"})
        (core/attr-create "test" "study" {:name "age" :type "long" :description "Customer age"})
        (is (= true (core/entity-create "test" "study" {:study/name "John" :study/age 65})))
        (let [entity (first (core/entity-list "test" "study"))]
          (is (= "John" (:study/name entity)))
          (is (= 65 (:study/age entity))))))))
