# entity-db

Entity-db provides a JSON API to manage database schema and perform CRUD operations on entities.
It is written in Clojure on top of

* [Datomic](http://www.datomic.com) - it's a NoSQL database with very rich modelling/querying capabilities
* [Compojure](https://github.com/weavejester/compojure) - a minimalistic web routing library for JSON Web API
* [Ring](https://github.com/ring-clojure) - Web server interface library (similar to [Rack](http://rack.github.io/)

## Installation

	$ brew install leiningen
	$ lein install

## Usage

To run the server at localhost:3000

	$ lein ring server-headless

## Running tests

	$ lein test

## Examples

### Create db "nuh-development"

	curl -X POST -d '{"dbname": "nuh-development"}' -H "Content-Type: application/json" localhost:3000/dbs

### List all databases

	curl localhost:3000/testing/entities

### Delete database nuh-development

	curl -X DELETE localhost:3000/dbs/nuh-development

### Create entity attribute customer/name in the "test" db (this also creates an entity the first time it is used)

	curl -X POST -d '{"attr":{"name": "name", "description": "Customer name"}}' -H "Content-Type: application/json" localhost:3000/test/entities/customer/attrs

### Create customer/age (number)

	curl -X POST -d '{"attr":{"name": "age", "description": "Customer age", "type": "long"}}' -H "Content-Type: application/json" localhost:3000/test/entities/customer/attrs

### Create customer 1

	curl -X POST -d '{"attrs":{"customer/name": "Karol Hosiawa", "customer/age": 35}}' -H "Content-Type: application/json" localhost:3000/test/entities/customer

### Create customer 2

	curl -X POST -d '{"attrs":{"customer/name": "Paul Syrysko", "customer/age": 49}}' -H "Content-Type: application/json" localhost:3000/test/entities/customer

### List all customers

	curl localhost:3000/test/entities/customer

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## Author

Karol Hosiawa <hosiawak@gmail.com>

## License

Copyright © 2015 Propheris

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
