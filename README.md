# Sample Spring Auditing
A sample project in Spring Boot 2.1.x to demonstrate security auditing via JPA, Spring Security, and Apache Kafka.

## Identity Management
Identity management is provided by a simple USER_ACCOUNT table with [BCrypt](https://en.wikipedia.org/wiki/Bcrypt) hashed passwords. Entity definition 
can be found in [UserAccount.java](src/main/java/us/vanderlugt/sample/audit/user/UserAccount.java). 

## Authentication


## License

This project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).