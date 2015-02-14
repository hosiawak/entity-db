(ns entity-db.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.util.response :refer [response]]
            [entity-db.core :as core]))

(defroutes app-routes
  (GET "/dbs" [] (response (core/db-list)))
  (POST "/dbs" [dbname] (response (core/db-create dbname)))
  (DELETE "/dbs/:dbname" [dbname] (response (core/db-delete dbname)))
  (GET "/:dbname/entities" [dbname] (response (core/entity-type-list dbname)))
  (GET "/:dbname/entities/:entity-name/attrs" [dbname, entity-name] (response (core/attr-list dbname entity-name)))
  (POST "/:dbname/entities/:entity-name/attrs" [dbname, entity-name, attr] (response (core/attr-create dbname entity-name attr)))
  (GET "/:dbname/entities/:entity-name" [dbname, entity-name] (response (core/entity-list dbname entity-name)))
  (POST "/:dbname/entities/:entity-name" [dbname, entity-name, attrs] (response (core/entity-create dbname entity-name attrs)))
  (route/not-found "Not Found"))

(def app
  (wrap-json-params
   (wrap-json-response
    (wrap-defaults app-routes
                   (assoc api-defaults
                          :responses {:content-type "application/json"} )))))
