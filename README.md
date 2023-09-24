# api

## Installation
Download and install Java Development Kit (JDK) v13.0 or later (do not use the Oracle JVM)
 
Install Mysql v8.0

## Spring Configuration Profiles

The default profiles are set to `dev,local`. With this setting, the order in which the 
application.yml files will be read are as follows the last one loaded overwrites the previous:

- application.yml
- application-dev.yml
- application-local.yml

The `application-local.yml` should _not_ be checked into source. This is the file that lives locally
to your specific development environment.

For other environments, like stage and prod, the active profiles are easily changed with an [environment variable](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-set-active-spring-profiles): `SPRING_PROFILES_ACTIVE`

For instance:

```
$ export SPRING_PROFILES_ACTIVE=stage
```

will load configuration files as follows:

- application.yml
- application-stage.yml

### Connecting with MySql Database
Create a database called 'Tuturing'

```sql
CREATE DATABASE tuturing CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

Inside the application.yml file of your resources folder, configure the following configurations to set-up a MySQL server:

``` 
Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url = jdbc:mysql://localhost:3306/tuturing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username = root //normally put your MySQL username 
spring.datasource.password = YOUR_MYSQL_PASSWORD
s
## Hibernate Properties
# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto = update
```

Switch to your working branch.

To install all dependencies and start the server, run the command:

`./gradlew bootRun`

The tasks command lists Gradle tasks that you can also invoke, including those added by the base plugin, and custom tasks you just added as well

`./gradlew tasks`

## Running Unit Tests

### Running Unit Tests With Gradle

You can run unit tests by executing the following command:

`./gradlew test`

And to generate test coverage reports:

`./gradlew jacocoTestReport`

## SOAP

### Logging

To enable SOAP messages logging set the log level of `org.apache.cxf` to INFO

```
org.apache.cxf: INFO
```

### Generating SOAP clients

Download wsdl2java on a linux box (not mac)

Always generate namespaced clients.
For every imported schema create new package/namespace using `-p` parameter. See TripSearch.
Try to avoid binding mappings (`-b`) - try `-p` first

## Documentation
http://localhost:8080/swagger-ui.html

## Health, Info, Metrics
http://localhost:8081/actuator
http://localhost:8081/actuator/health
http://localhost:8081/actuator/info

# Generating JWT RSA256 key

`openssl genrsa -out jwtkeypair.pem 4096`

`openssl rsa -in jwtkeypair.pem -pubout -out jwtpublickey.crt`

`openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in jwtkeypair.pem -out jwtpkcs8.key`

Output:
* jwtpublickey.crt - public key
* jwtpkcs8.key - private key

# Database migrations

## Create the first (base) changelog file

The base changelog file has been generated with the following command:

`./gradlew generateChangelog`

Note: do not regenrate the base changelog file.


## Marking changes as deployed

To mark the changes as already applied
(in case of working with preexisting schema)
run the following command. It will mark all
the changelog items as already applied.

`./gradlew changeLogSync`

## Updating the database to the latest schema

Run:

`./gradlew update`

## Creating migration

First, make sure your database schema is in the latest version

`./gradlew update`

Then, create a snapshot of the database before applying any changes to the database:

`./gradlew snapshot`

Make any changes required to the database schema.

Review the difference between previous schema and the changes:

`./gradlew diff`

If everything looks good, update the database changelog

`./gradlew diffChangeLog`

It will overwrite `liquibaseDiff.json` file.
Copy the list of changesets from the diff file and
add those changes to `src/main/resources/db/changelog.json`

Mark your database changes as deployed

`./gradlew changeLogSync`

## Adding a data export into the database changelog

Manually add a new changest to the changelog.
Make sure to set new changeset id.

```json
  {
    "changeSet": {
      "id": "1585900949054-1",
      "author": "Wojciech (manual)",
      "changes": [
        {
          "sqlFile": {
            "path": "scripts/dkNumbers2.sql"
          }
        }]

    }
  }
```

## Debugging

Check liquibase settings in build.gradle.kts,
especially the database connection string