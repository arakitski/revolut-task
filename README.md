# revolut-task
Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Implementation details
* Build tool - [Maven](https://maven.apache.org/)
* Resp Api - [Spark](http://sparkjava.com/)
* Dependency Injection - [Guice](https://github.com/google/guice/wiki)
* Database - [H2](https://www.h2database.com/html/main.html)
* Access to DB -  [jOOQ](https://www.jooq.org/)

## Running application
To run application you need to [install maven](https://maven.apache.org/install.html). 
Next, call from the command line:
```
mvn clean install exec:java -Dexec.args="--port=9011"
```
If port argument is not defined will be used 1234 by default.

## Api

* `GET` /accounts - gets all accounts
* `POST` /accounts  Body: `{"balance":  10.0}` - creates account with initial balance 
* `GET` /accounts/{account_id} - gets account by id
* `DELETE` /accounts/{account_id} - deletes account by id
* `POST` /transfer?fromId={account_id}&toId={account_id}&amount={value}  - transfers money between accounts