# Sample Spring Auditing
A sample project in Spring Boot 2.1.x to demonstrate security auditing via JPA, Spring Security, and Apache Kafka.

## Setup 
```
docker run -it --rm --network host --name postgres -e 'POSTGRES_USER=mark' -e 'POSTGRES_PASSWORD=H3lpM#Plz' postgres
```


## Identity Management
Identity management is provided by a simple USER_ACCOUNT table with [BCrypt](https://en.wikipedia.org/wiki/Bcrypt) hashed passwords. Entity definition 
can be found in [UserAccount.java](src/main/java/us/vanderlugt/sample/audit/user/UserAccount.java). 

## Authentication


## JWT Signing Key Generated via Keytool
[Keytool Reference](https://docs.oracle.com/javase/10/tools/keytool.htm)
```
keytool -genkeypair -alias jwt -keyalg RSA \
  -keystore jwt.jks -storepass 'H3lpM#Plz' \
  -dname "CN=Mark Vander Lugt,OU=Unknown,O=Unknown,L=Little Rock,S=AR,C=US" 
```
And the certificate can be exported using keytool and openssl
```
keytool -list -rfc --keystore jwt.jks | openssl x509 -inform pem -pubkey
```

## License

This project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

