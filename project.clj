(defproject entity-db "0.1.0-SNAPSHOT"
  :description "Entity DB"
  :url "http://github.com/Propheris/entity-db"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                 :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.datomic/datomic-pro "0.9.5130" :exclusions [joda-time]]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler entity-db.handler/app}
  :main ^:skip-aot entity-db.core
  :target-path "target/%s"
  :profiles
  {
   :uberjar {:aot :all}
   :dev {
         :dependencies [[javax.servlet/servlet-api "2.5"]
                       [ring-mock "0.1.5"]]
         :plugins [[cider/cider-nrepl "0.8.0"]]}}
  :jvm-opts ["-Xmx1g"])
