(ns entity-db.core
  (:gen-class)
  (:require [datomic.api :as d]
            [clojure.walk :refer [keywordize-keys]]))

(def uri "datomic:mem://")

(def type-attr
  "Name of the 'type' attribute which is needed to lookup entities"
  "type_")

(defn db-list
  "List all databases"
  []
  (or (d/get-database-names (str uri "*"))
      []))

(defn conn
  "Connect to dbname"
  [dbname]
  (d/connect (str uri dbname)))

(defn db-create
  "Create database"
  [dbname]
  (d/create-database (str uri dbname)))

(defn db-delete
  "Delete database"
  [dbname]
  (d/delete-database (str uri dbname)))

(defn entity-type-list
  "List entity types"
  [dbname]
  (sort (map #(namespace %)
             (d/q '[:find [?v ...]
                    :in $ ?type-attr
                    :where [_ :db.install/attribute ?a]
                    [?a :db/ident ?v]
                    [(name ?v) ?name]
                    [(= ?type-attr ?name)]] (d/db (conn dbname)) type-attr))))

(defn attr-list
  "List entity attributes"
  [dbname entity-name]
  (map (fn [e] (let [entity (d/touch (d/entity (d/db (conn dbname)) e))]
                 {:id (get entity :db/id)
                  :name (get entity :db/ident)
                  :description (get entity :db/doc)
                  :type (name (get entity :db/valueType))
                  :cardinality (name (get entity :db/cardinality))
                  :fulltext (get entity :db/fulltext)}))
       (d/q '[:find [?a ...] :in $ ?ns
              :where [_ :db.install/attribute ?a]
              [?a :db/ident ?name]
              [(namespace ?name) ?attr-ns]
              [(= ?attr-ns ?ns)]] (d/db (conn dbname)) entity-name)))

(defn- type-attr-create-if-missing
  "Creates :entity-name/type in the schema if missing"
  [dbname entity-name]
  (if (some #{(keyword entity-name "type")} (attr-list dbname entity-name))
    nil
    @(d/transact (conn dbname)
                 [{:db/id #db/id[:db.part/db]
                   :db/ident (keyword entity-name type-attr)
                   :db/valueType (keyword "db.type" "string")
                   :db/cardinality (keyword "db.cardinality" "one")
                   :db/fulltext false
                   :db/doc (str entity-name "type")
                   :db.install/_attribute :db.part/db}])))

;; TODO add validations to :type, :cardinality, :fulltext
(defn attr-create
  "Create entity attribute"
  [dbname entity-name attr]
  (do
    (type-attr-create-if-missing dbname entity-name)
    @(d/transact (conn dbname)
                 [{:db/id #db/id[:db.part/db]
                   :db/ident (keyword entity-name (get attr :name))
                   :db/valueType (keyword "db.type" (or (get attr :type) "string"))
                   :db/cardinality (keyword "db.cardinality" (or (get attr :cardinality) "one"))
                   :db/fulltext (or (get attr :fulltext) false)
                   :db/doc (get attr :description)
                   :db.install/_attribute :db.part/db}])
    true))



(defn entity-create
  "Create entity"
  [dbname entity-name attrs]
  (do
    @(d/transact (conn dbname)
                 [(assoc (keywordize-keys attrs)
                         :db/id #db/id[:db.part/user]
                         (keyword entity-name type-attr) entity-name)])
    true))

(defn entity-list
  "List entities"
  [dbname entity-name]
  (map (fn [e]
         (assoc (into {}
                      (d/touch
                       (d/entity (d/db (conn dbname)) e)))
                (keyword entity-name "id") e))
       (d/q '[:find [?e ...]
              :in $ ?attr-name ?entity-name
              :where [?e ?attr-name ?entity-name]]
         (d/db (conn dbname)) (keyword entity-name type-attr) entity-name)))
