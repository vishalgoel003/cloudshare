# Cloud-share
## A cloud provisioning service

This is a spring boot application. The default database it uses is H2.
If you wish to run it with some other RDBMS, you will find the instructions in later portion of this page.

Assuming you have Java 8+ on your system, clone the repository and run following command inside the cloned directory to start the application:

`./mvnw spring-boot:run`

## Supported functions

### Basic:

1. API for User-sign-up - master and non-master account (includes following info: Name, email-address, mobile no, password) -> email and mobile no must be unique , where Master account can scroll details of all user VMs while non-master account can view only his vm details. Master account has right delete other user accounts.
2. API to obtain JWT auth token based upon sign-up credentials
3. API for Requesting VM provisioning with following details: OS, RAM, Hard-disk and CPU cores.
4. API for displaying list of VMs requested by particular user.
5. API to list the top 'n' VMs by Memory for logged in user
6. API to delete the user account, allowed. If account gets deleted, all registered vms must be deleted also
7. API to list the top 'n' VMs by memory across all users in system
8. Raise Proper Exception and generate logs if API fails to execute (may be invalid input or checks for numeric / alpha value)
9. Every API call must be be authenticated using JWT token in request header. (except API sign-up request)
10. Prepare Postman collection for testing REST end-points above.
11. ~~Database Postgres (hibernate)~~ (Default database is in-memory H2, instruction to switch data store can be found in following sections).

### Advanced (TBC):

- Management API to register / de-register server
- Load management batch
- Caching (Redis TTL based)

## Database modelling

**User ---< VirtualMachine >--- Server**

- User's login handle is email.
- User can oun Virtual Machines.
- Virtual Machines run on Server.

## Switching the data-store

We have used hibernate based queries to make the system database agnostic.

To simulate another data-store, you may run docker image. And change application properties in file **src/main/resources/application.properties** to point your data-store container.

Properties to change:

```spring.datasource.url
spring.datasource.driverClassName
spring.datasource.username
spring.datasource.password
spring.jpa.database-platform
```
