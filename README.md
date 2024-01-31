# Users API
*Spring Boot REST API for Users management*<br>

### Get Started

* Clone this repository using either SSH or HTTP at https://github.com/claytonrm/users-api
* Install requirements

### Requirements
- [Java 17](https://jdk.java.net/java-se-ri/17)
- [Lombok](https://projectlombok.org/download)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [MongoDB](https://www.mongodb.com/) - Might be ignored (You can use it through Docker Compose)

### Setup
Set your docker up following the steps below to setup the project locally:

##### Create the artifact
 ```shell 
 mvn clean package -DskipTests
 ```
##### Setup Environment and Images
 ```shell 
docker-compose up -d --build
 ```
### Running App

You're now able to create requests. Here's an example to create a random user:

```
curl  -X POST \
  'http://localhost:8080/users' \
  --header 'Accept: */*' \
  --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "name": "Josh",
  "cpf": "130.877.567-92",
  "email": "josh@something.com",
  "birthDate": "20/01/1990"
}'

```

See full documentation at http://localhost:8080/swagger-ui/index.html#/

### Running Tests
```shell
mvn test
```
