# About

This is a REST service client abstraction. In other words, this is a library containing an abstraction of a client for use with a RESTful web service.

This library assumes and operates on a set of conventions around CRUD and query operations, which the RESTful web service endpoints should ideally abide by. The service response REST conventions are mirrored by an equivalent set of conventions in this client library.

[![Build Status](https://travis-ci.org/edinhodzic/jersey-rest-client.svg?branch=master)](https://travis-ci.org/edinhodzic/jersey-rest-client)

## Service REST and client API conventions

| HTTP Method | Description | Collection URI HTTP response       | Item URI HTTP response             | Client API response               |
|-------------|-------------|------------------------------------|------------------------------------|-----------------------------------|
| POST        | Create      | `201 Created` / `409 Conflict`?    | unsupported                        | `Success` / `Failure`             |
| GET         | Read        | unsupported but probably should be | `200 Ok` / `404 Not Found`         | `Success(Some)` / `Success(None)` |
| PUT         | Update      | unsupported                        | `204 No Content` / `404 Not Found` | TODO : implement                  |
| DELETE      | Delete      | unsupported                        | `204 No Content` / `404 Not Found` | TODO : implement                  |

    TODO add query endpoint convention

<sup>**Tip** : use the [`jersey-rest-service`](https://github.com/edinhodzic/jersey-rest-service) library to develop RESTful web services which use the above conventions</sup>

<sup>**Top tip** : use the [`jersey-rest-service-archetype`](https://github.com/edinhodzic/jersey-rest-service-archetype) to very quickly create RESTful web service projects from scratch which use the above conventions</sup>

# What's under the hood?

Implementation:

- [Scala](http://www.scala-lang.org/)
- [Jersey client API](https://jersey.java.net/documentation/latest/client.html)
- [Logback](http://logback.qos.ch/)

Testing:

- [Specs2](https://etorreborre.github.io/specs2/)

# Quick start implementation

Suppose we were implementing a client for a user REST service; a `UserRestClient`.

## Example 1 - minimal implementation
```scala
class UserRestClient (url: String, username: String, password: String)
  extends AbstractRestClient[User](url, username, password)
```
## Example 2 - wired up with Spring
```scala
@Component
class UserRestClient @Autowired()
(@Value("service.user.url") url: String,
 @Value("service.user.username") username: String,
 @Value("service.user.password") password: String)
  extends AbstractRestClient[User](url, username, password)
```
# Quick start usage

Following on from the above hypothetical user REST service and client scenario.

Add Maven dependency to your project:
```xml
<dependency>
  <groupId>com.edinhodzic.client</groupId>
  <artifactId>jersey-rest-client</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```
Instantiate a `UserRestClient`:
```scala
val userRestClient: UserRestClient = new UserRestClient(
"http://api.example.com:9023", "us3rn4m3", "p4s5w0rd")
```
Create a user:
```scala
// post user and pattern match on Try[User]
userRestClient post new User("me") match {
  case Success(user) => ...
  case Failure(throwable) => ...
}
```
Read a user:
```scala
// get user and pattern match on Try[Option[User]]
userRestClient get "5627a1764568cdf041e0996e" match {
  case Success(maybeUser) => maybeUser match {
    case Some(user) => ...
    case None => ...
  }
  case Failure(throwable) => ...
}
```
Update a user:

    // TODO implement

Delete a user:
```scala
// delete user and pattern match on Try[Option[User]]
userRestClient delete "5627a1764568cdf041e0996e" match {
  case Success(option) => option match {
    case Some() => ...
    case None => ...
  }
  case Failure(throwable) => ...
}
```
Query user service:

    // TODO implement
    
# What's next?

## Incomplete features

There are incomplete features in this client. For example and as above, it should carry out CRUD and query operations yet only the create operation is currently implemented.

## Future development ideas

- [ ] _Monitor and gracefully handle remote system call failures_ : implement [circuit breaker](http://martinfowler.com/bliki/CircuitBreaker.html) pattern
- [ ] _Refactor to inline with [`jersey-rest-service`](https://github.com/edinhodzic/jersey-rest-service) library_ : The conventions used in this project are perfectly aligned to RESTful web services developed using the [`jersey-rest-service`](https://github.com/edinhodzic/jersey-rest-service) library. For this reason it may make sense to have this and that project as Maven modules under one Maven project.
- [ ] implement a performant non-HTTP client for use on service to service communication