# About

This is a REST service client abstraction. In other words, this is a library containing an abstraction of a client for use with a RESTful web service.

This library assumes and operates on a set of conventions around CRUD and query operations, which the RESTful web service endpoints should ideally abide by. The service response REST conventions are mirrored by an equivalent set of conventions in this client library.

## Service REST and client API conventions

| HTTP Method | Description | Collection URI HTTP response       | Item URI HTTP response             | Client API response               |
|-------------|-------------|------------------------------------|------------------------------------|-----------------------------------|
| POST        | Create      | `201 Created` / `409 Conflict`?    | unsupported                        | `Success` / `Failure`             |
| GET         | Read        | unsupported but probably should be | `200 Ok` / `404 Not Found`         | `Success(Some)` / `Success(None)` |
| PUT         | Update      | unsupported                        | `204 No Content` / `404 Not Found` | TODO : implement                  |
| DELETE      | Delete      | unsupported                        | `204 No Content` / `404 Not Found` | TODO : implement                  |

    TODO add query endpoint convention

<sup>**Tip** : use the [`jersey-rest-service`](https://github.com/edinhodzic/jersey-rest-service) library to develop RESTful web services which use the above conventions</sup>

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

    class UserRestClient (url: String, username: String, password: String)
      extends AbstractRestClient[User](url, username, password)

## Example 2 - wired up with Spring

    @Component
    class UserRestClient @Autowired()
    (@Value("service.user.url") url: String,
     @Value("service.user.username") username: String,
     @Value("service.user.password") password: String)
      extends AbstractRestClient[User](url, username, password)

# Quick start usage

Following on from the above hypothetical user REST service and client scenario.

Add Maven dependency to your project:

    <dependency>
      <groupId>com.edinhodzic.client</groupId>
      <artifactId>jersey-rest-client</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

Instantiate a `UserRestClient`:

    val userRestClient: UserRestClient = new UserRestClient(
    "http://api.example.com:9023", "us3rn4m3", "p4s5w0rd")

Create a user:

    // post user and pattern match on Try[User]
    userRestClient post new User("me") match {
      case Success(user) => ...
      case Failure(throwable) => ...
    }
    
# What's next?

## Incomplete features

There are incomplete features in this client. For example and as above, it should carry out CRUD and query operations yet only the create operation is currently implemented.

## Future development ideas

- _Monitor and gracefully handle remote system call failures_ : implement [circuit breaker](http://martinfowler.com/bliki/CircuitBreaker.html) pattern
