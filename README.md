# Cloud-share
## A cloud provisioning service

This is a spring boot application. The default database it uses is H2.
If you wish to run it with some other RDBMS, you will find the instructions in later portion of this page.

Assuming you have Java 8+ on your system, clone the repository and run following command inside the cloned directory to start the application:

`./mvnw spring-boot:run`

## Supported functions

### Basic:

1. API for user signup - Fields: Name, email-address, mobile no, password, role. Email and mobile no, ~~role~~ must be unique. 
Role can be master (ADMIN) and non-master (NON_ADMIN). Master accounts can view details of all user VMs while the non-master accounts can view only their own vm details. Master account can delete other user accounts.
2. API to obtain JWT token based on credentials
3. API for Requesting VM provisioning with following details: OS, RAM, Hard-disk and CPU cores.
4. API for displaying list of VMs requested by particular user.
6. API to list the top 'n' VMs by Memory for logged in user
7. API to delete the user account. If account gets deleted, all registered vms must be deleted also
8. API to list the top 'n' VMs by memory across all users in system
9. Raise Proper Exception and generate logs if API fails to execute (may be invalid input or checks for numeric / alpha value)
10. Every API call must be authenticated using the JWT token in the request header. (except sign-up API)
11. Prepare Postman collection for testing REST end-points above. 

### Advanced (TBC):

- Management API to register / de-register server
- Load management batch
- Caching (Redis TTL based)

## Database modelling

**User ---< VirtualMachine >--- Server**

- User's login handle is email.
- User can own Virtual Machines.
- Virtual Machines run on Server.

## Switching the data-store

We have used hibernate based queries to make the system database agnostic.

To simulate another data-store, you may run docker image. And change application properties in file **src/main/resources/application.properties** to point your data-store container.

Properties to change (uncomment the sample properties):
```
spring.datasource.url
spring.datasource.driverClassName
spring.datasource.username
spring.datasource.password
spring.jpa.database-platform
```

### Running with Postgres:

Run a postgres docker container:
```
docker run --name postgres-container -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```
Add (or uncomment) the following dependency in pom.xml:
```$xslt
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Configure (or uncomment) the following settings in `application.properties`
```
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect
```

For removing container you can use:
```
docker stop postgres-container && docker system prune -f
```

***Note***: With above settings, postgres did not allow to create a table with name **_user_** and had to change the table name to _**application_user**_ in entity definition.