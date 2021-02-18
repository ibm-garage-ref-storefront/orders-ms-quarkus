##### orders-ms-quarkus

# Microservice Apps Integration with MariaDB Database and enabling OpenID Connect protection for APIs

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://cloudnativereference.dev/*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Running the application](#running-the-application)
    + [Get the Orders application](#get-the-orders-application)
    + [Run the MariaDB Docker Container](#run-the-mariadb-docker-container)
    + [Set Up Keycloak](#set-up-keycloak)
    + [Run the Orders application](#run-the-orders-application)
    + [Validating the application](#validating-the-application)
    + [Exiting the application](#exiting-the-application)
* [Conclusion](#conclusion)
* [References](#references)

## Introduction

This project will demonstrate how to deploy a Quarkus application with a MariaDB database. This application provides basic operations of saving and querying orders from a relational database as part of the Orders service of Storefront.

![Application Architecture](static/orders.png?raw=true)

Here is an overview of the project's features:
- Leverages [`Quarkus`](https://quarkus.io/), the Supersonic Subatomic Java Framework.
- Uses [MariaDB](https://mariadb.org/) as the orders database.
- When retrieving orders using the OAuth 2.0 protected APIs, return only orders belonging to the user identity encoded in the `user_name` claim in the JWT payload. For more details, check this [doc](https://cloudnativereference.dev/related-repositories/keycloak).

### APIs

The Orders Microservice REST API is OAuth 2.0 protected. These APIs identifies and validates the caller using JWT tokens.

* `GET /micro/orders`

  Returns all orders. The caller of this API must pass a valid OAuth token. The OAuth token is a JWT with the orders ID of the caller encoded in the `user_name` claim. A JSON object array is returned consisting of only orders created by the orders ID.

* `GET /micro/orders/{id}`

  Return order by ID. The caller of this API must pass a valid OAuth token. The OAuth token is a JWT with the orders ID of the caller encoded in the `user_name` claim. If the id of the order is owned by the orders passed in the header, it is returned as a JSON object in the response; otherwise `HTTP 401` is returned.

* `POST /micro/orders`

  Create an order. The caller of this API must pass a valid OAuth token. The OAuth token is a JWT with the orders ID of the caller encoded in the `user_name` claim. The Order object must be passed as JSON object in the request body with the following format.

  ```
    {
      "itemId": "item_id",
      "count": "number_of_items_in_order",
    }
  ```

  On success, you will see a success message something like below.

  ```
    {
      "id":"e788488b-ccda-4830-9ec8-8ee76cc89b58",
      "date":1613641596736,
      "itemId":13401,
      "customerId":"user",
      "count":1
    }
  ```

## Pre-requisites:

* [Java](https://www.java.com/en/)

## Running the application

### Get the Orders application

- Clone orders repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/orders-ms-quarkus.git
cd orders-ms-quarkus
```

### Run the MariaDB Docker Container

Run the below command to get MariaDB running via a Docker container.

```bash
# Start a MariaDB Container with a database user, a password, and create a new database
docker run --name ordersmysql \
    -e MYSQL_ROOT_PASSWORD=admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=ordersdb \
    -p 3307:3306 \
    -d mariadb
```

If it is successfully deployed, you will see something like below.

```
$ docker ps
CONTAINER ID   IMAGE                              COMMAND                  CREATED             STATUS             PORTS                              NAMES
00b8663e44a1   mariadb                            "docker-entrypoint.s…"   About an hour ago   Up About an hour   0.0.0.0:3307->3306/tcp             ordersmysql
```

- Create the orders table.

Firstly, `ssh` into the MariaDB container.

```
docker exec -it ordersmysql bash
```

- Now, run the below command for table creation.

```
mysql -udbuser -ppassword
```

- This will take you to something like below.

```
root@5762edb59a86:/# mysql -udbuser -ppassword
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 22
Server version: 10.5.5-MariaDB-1:10.5.5+maria~focal mariadb.org binary distribution

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]>
```

- Go to `scripts > mysql_data.sql`. Copy the contents from [mysql_data.sql](./scripts/mysql_data.sql) and paste the contents in the console.

- You can exit from the console using `exit`.

```
MariaDB [(none)]> exit
Bye
```

- To come out of the container, enter `exit`.

```
root@5762edb59a86:/# exit
```

## Set Up Keycloak

In storefront, Keycloak is used for storing users and authenticating users. To configure it, refer [Keycloak - JWT token generation](https://cloudnativereference.dev/related-repositories/keycloak/).

### Run the Orders application

#### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -Dquarkus.datasource.jdbc.url=jdbc:mysql://localhost:3307/ordersdb?useSSL=true -Dquarkus.datasource.username=dbuser -Dquarkus.datasource.password=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826
```

If it is successful, you will see something like this.

```
[INFO]
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ orders-ms-quarkus ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- quarkus-maven-plugin:1.11.2.Final:dev (default-cli) @ orders-ms-quarkus ---
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-02-18 16:22:18,769 INFO  [io.quarkus] (Quarkus Main Thread) orders-ms-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.11.2.Final) started in 1.838s. Listening on: http://localhost:8080
2021-02-18 16:22:18,772 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2021-02-18 16:22:18,772 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [agroal, cdi, jdbc-mysql, mutiny, narayana-jta, oidc, resteasy, resteasy-jackson, security, smallrye-context-propagation]
```

#### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `orders-ms-quarkus-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using the below command.

```
java -jar -Dquarkus.datasource.jdbc.url=jdbc:mysql://localhost:3307/ordersdb?useSSL=true -Dquarkus.datasource.username=dbuser -Dquarkus.datasource.password=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -jar target/orders-ms-quarkus-1.0.0-SNAPSHOT-runner.jar
```

If it is run successfully, you will see something like below.

```
$ java -jar -Dquarkus.datasource.jdbc.url=jdbc:mysql://localhost:3307/ordersdb?useSSL=true -Dquarkus.datasource.username=dbuser -Dquarkus.datasource.password=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -jar target/orders-ms-quarkus-1.0.0-SNAPSHOT-runner.jar
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-02-18 16:33:24,393 INFO  [io.quarkus] (main) orders-ms-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.11.2.Final) started in 6.147s. Listening on: http://0.0.0.0:8080
2021-02-18 16:33:24,396 INFO  [io.quarkus] (main) Profile prod activated.
2021-02-18 16:33:24,396 INFO  [io.quarkus] (main) Installed features: [agroal, cdi, jdbc-mysql, mutiny, narayana-jta, oidc, resteasy, resteasy-jackson, security, smallrye-context-propagation]
```

#### Creating a native executable

Note: In order to run the native executable, you need to install GraalVM. For instructions on how to install it, refer [this](https://quarkus.io/guides/building-native-image).

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

You can then execute your native executable with the below command:

```
./target/orders-ms-quarkus-1.0.0-SNAPSHOT-runner -Dquarkus.datasource.jdbc.url=jdbc:mysql://localhost:3307/ordersdb?useSSL=true -Dquarkus.datasource.username=dbuser -Dquarkus.datasource.password=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826
```

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

#### Running the application using docker

- Build the JVM docker image and run the application.

Package the application.
```shell script
./mvnw package -Dquarkus.native.container-build=true
```

Build the docker image using `Dockerfile.jvm`.
```shell script
docker build -f src/main/docker/Dockerfile.jvm -t orders-ms-quarkus .
```

Run the application.
```shell script
docker run -it -d --rm -e quarkus.datasource.jdbc.url=jdbc:mysql://host.docker.internal:3307/ordersdb?useSSL=true -e quarkus.datasource.username=dbuser -e quarkus.datasource.password=password -e quarkus.oidc.auth-server-url=http://host.docker.internal:8085/auth/realms/sfrealm -e quarkus.oidc.client-id=bluecomputeweb -e quarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -p 8086:8080 orders-ms-quarkus
```

- Build the native docker image and run the application.

For native docker image, package the application using native profile.
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Build the docker image using `Dockerfile.native`.
```shell script
docker build -f src/main/docker/Dockerfile.native -t orders-ms-quarkus-native .
```

Run the application.
```shell script
docker run -it -d --rm -e quarkus.datasource.jdbc.url=jdbc:mysql://host.docker.internal:3307/ordersdb?useSSL=true -e quarkus.datasource.username=dbuser -e quarkus.datasource.password=password -e quarkus.oidc.auth-server-url=http://host.docker.internal:8085/auth/realms/sfrealm -e quarkus.oidc.client-id=bluecomputeweb -e quarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -p 8086:8080 orders-ms-quarkus-native
```

### Validating the application

- Now generate a JWT Token.

To do so, run the commands below:

```
export access_token=$(\
    curl -X POST http://<REPLACE_ME_WITH_KEYCLOAK_HOST_NAME>:<REPLACE_ME_WITH_KEYCLOAK_PORT>/auth/realms/<REPLACE_ME_WITH_REALM>/protocol/openid-connect/token \
    --user <REPLACE_ME_WITH_CLIENT_ID>:<REPLACE_ME_WITH_CLIENT_SECRET> \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=<REPLACE_ME_WITH_USERNAME>&password=<REPLACE_ME_WITH_PASSWORD>&grant_type=password' | jq --raw-output '.access_token' \
 )
```

If successful, you will see something like below.

```
$ export access_token=$(\
    curl -X POST http://localhost:8085/auth/realms/sfrealm/protocol/openid-connect/token \
    --user bluecomputeweb:a297757d-d2cc-4921-8e66-971432a68826 \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=user&password=password&grant_type=password' | jq --raw-output '.access_token' \
 )
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  2217  100  2166  100    51  15169    357 --:--:-- --:--:-- --:--:-- 15146
```

- To validate the token, you can print the `access_token`.

```
$ echo $access_token
eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTNWNfNWFuT1dsb3RsaFlxSFJjY0l4d3ROa1dTcXpQVU1SWkliWWVaYm1ZIn0.eyJleHAiOjE2MTM2NTQ0NDIsImlhdCI6MTYxMzY1Mzg0MiwianRpIjoiZTYwZTdmZWUtNWM1MS00YTAxLWJlYjItNzQ4NmY0OGQ4OWM2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg1L2F1dGgvcmVhbG1zL3NmcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMjJjMWYyNDAtMWNjZS00NjhlLWE0YjAtMWQ3NTFlYmNiOTM1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmx1ZWNvbXB1dGV3ZWIiLCJzZXNzaW9uX3N0YXRlIjoiOGRkZjhhM2MtNDlkYy00ZjgwLWIxOWYtMGNjMWU5MWZhMWI5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ1c2VyIGxhc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6InVzZXIiLCJmYW1pbHlfbmFtZSI6Imxhc3QiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20ifQ.Feee9MFZDMFGu1RV8Kbx6ZLcsc6YPTLq4bGUkFAU7oCnkII7st6Ozo9nxEK2rj4cT_-fbzs_YBErLwdcA662qZfU868jL0q0SRvNoqCjU90hf552QhkP3UdlfDhYdM683UAe1x5Kww-mraqDi8tM1rIQ6XFXf6kvJEp7ij40lCX26_D7XDLiRtf-tQ_aLKb3R_FF5oVYo50KR4Au3UyA0lKoy8f8CCzVX7XmvPOzCS2wZErIdDCuYVSCCVawIvzYZbhIr977sL6mNftBStP4GC_BoGllDrEhis6wvqy9aQL8-xjQGnk_WQ83vh8zxRhaVu-D9Ol-93kjX8FUyrzT0w
```

- To create an order, run the following to create an order for the user. Be sure to export the JWT to `access_token` as shown earlier.

```
curl -k -X POST   --url http://<Orders_Service_Host>:<Orders_Service_Port>/micro/orders -H "Authorization: Bearer "$access_token -H "Content-Type: application/json" -d '{"itemId":13402, "count":1}'
```

If successfully created, you will see something like below.

```
$ curl -k -X POST   --url http://localhost:8086/micro/orders -H "Authorization: Bearer "$access_token -H "Content-Type: application/json" -d '{"itemId":13402, "count":1}'
{"id":"9cf526ad-40eb-4828-8e55-643609e0999c","date":1613654490757,"itemId":13402,"customerId":"user","count":1}
```

- To get all orders, run the following to retrieve all orders for the given customerId. Be sure to export the JWT to `access_token` as shown earlier.

```
curl -v -X GET   http://<Orders_Service_Host>:<Orders_Service_Port>/micro/orders   -H "Authorization: Bearer "$access_token
```

If successfully created, you will see something like below.

```
$ curl -v -X GET   http://localhost:8086/micro/orders   -H "Authorization: Bearer "$access_token
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8086 (#0)
> GET /micro/orders HTTP/1.1
> Host: localhost:8086
> User-Agent: curl/7.54.0
> Accept: */*
> Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTNWNfNWFuT1dsb3RsaFlxSFJjY0l4d3ROa1dTcXpQVU1SWkliWWVaYm1ZIn0.eyJleHAiOjE2MTM2NTUwODcsImlhdCI6MTYxMzY1NDQ4NywianRpIjoiNmFiNmI0MGUtNTE4Ni00MGZhLWIxNWItZjRhNjg0M2Y4YTk5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg1L2F1dGgvcmVhbG1zL3NmcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMjJjMWYyNDAtMWNjZS00NjhlLWE0YjAtMWQ3NTFlYmNiOTM1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmx1ZWNvbXB1dGV3ZWIiLCJzZXNzaW9uX3N0YXRlIjoiN2M3ODYzNTUtMmNmOC00YThhLTkzM2ItNzgwYzJiNGM4NTViIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ1c2VyIGxhc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6InVzZXIiLCJmYW1pbHlfbmFtZSI6Imxhc3QiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20ifQ.UhEmrvgWG9KZ9h8oJv0fJskU-yym9P-DMpsP9TBUFa_D770MDJsVxMKi77PteBFyyve5WRKWr76SNbnfBweR-_fYXx-oMIrsdCDk2eY1a8UoP7s0lPKh26c9Mhr3tX34koxQETvoAADMlGe4N21EaRdgXHtMZIqnzCS_8oYXJT6PjllpWDZsY1O5sUr459prr_dB7t1pZJezeU0hQveJJvbISPou7j87C-lqUFRqb13sEhKsynSE0dXkrxKkCWRnApJX17VjAs3Vd4nMd7w_Set5YnRoWelQqj7hGPJ8cKuB-HkIY2Ml-2xlhdf7yfieuPNHMVhA7kb_GbSrm-F-EA
>
< HTTP/1.1 200 OK
< Content-Length: 225
< Content-Type: application/json
<
* Connection #0 to host localhost left intact
[{"id":"9cf526ad-40eb-4828-8e55-643609e0999c","date":1613654491000,"itemId":13402,"customerId":"user","count":1},{"id":"e788488b-ccda-4830-9ec8-8ee76cc89b58","date":1613661396000,"itemId":13401,"customerId":"user","count":1}]
```

### Exiting the application

To exit the application, just press `Ctrl+C`.

If using docker, use `docker stop <container_id>`

## Conclusion

You have successfully developed and deployed the Orders Microservice and a MariaDB database locally using Quarkus framework.

## References

- https://quarkus.io/guides/getting-started
- https://quarkus.io/guides/config
- https://quarkus.io/guides/building-native-image
