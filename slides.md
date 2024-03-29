---
title: Quarkus
highlightTheme: tomorrow-night-blue
css: css/style.css
---
![Quarkus](img/quarkus-logo.png)
#### Supersonic Subatomic Java

---

* Quarkus is a Cloud Native, Container First Java framework
* Crafted from best-of-breed Java libraries and standards
* Tailored for GraalVM and OpenJDK HotSpot 

---

Alternative to Spring Boot, Micronaut 

---

### Container first

* Fast Startup 
* Low memory utilization 

Note: 
* (tens of milliseconds) allows automatic scaling up and down of microservices on containers and Kubernetes as well as Serverless / FaaS on-the-spot execution, scale to zero approach
* helps optimize container density in microservices architecture deployments requiring multiple containers

---

![Quarkus](img/quarkus_graphics_v3_bootmem_wide_03.png)

---

### Developer Joy

* Unified configuration with all configuration in a single property file.
 
* Zero config, streamlined code for the 80% common usages, flexible for the 20%

* Live reload, debugger starts by default on port 5005

---

* No hassle native executable generation:       
```commandline
mvn package -Pnative
``` 

* Quarkus extension framework reduces the complexity for making third-party frameworks run on Quarkus and compile to a GraalVM native binary

* Kotlin and Scala support

---

Use programming model you already know

---

→ Inject dependencies with CDI annotations

(but you can use Spring annotations as well)

---

→ Expose RESTful APIs using JAX-RS 

---

→ Talk to DB with JPA/Hibernate

---

### Eclipse MicroProfile support 

* Write fault-tolerant code
* Secure apps with MicroProfile JWT
* Monitor apps with MicroProfile OpenTracing, Metrics and Health

---

### Unifies Imperative and Reactive

Note: 
* Most Java developers are familiar with the imperative programming model and would like to utilize that experience when adopting a new platform 
* At the same time, developers are rapidly adopting a cloud native, event-driven, asynchronous, and reactive model to address business requirements to build highly concurrent and responsive applications 
* Quarkus is designed to seamlessly brings the two models together in the same platform

---

Imperative

```java
@Inject
SayService say;

@GET
@Produces(MediaType.TEXT_PLAIN)
public String hello() {
    return say.hello();
}
```

---

Reactive

```java
@Inject @Stream("kafka")
Publisher<String> reactiveSay;

@GET
@Produces(MediaType.SERVER_SENT_EVENTS)
public Publisher<String> stream() { 
    return reactiveSay; 
}
```

---

Get quarked! and let’s start building some hadrons

---

Create a new project:

```commandline
mvn io.quarkus:quarkus-maven-plugin:1.0.0.CR1:create \
    -DprojectGroupId=com.example \
    -DprojectArtifactId=movies-app \
    -DclassName="com.example.GreetingResource" \
    -Dpath="/hello"
```

---

Start application in dev mode: 

```commandline 
./mvnw compile quarkus:dev 
``` 
<!-- .element: style="text-align:center" -->

---

Create class `GreetingService`:

```java
@ApplicationScoped
class GreetingService {

    String greeting(String name) {
        return "Hello, " + name;
    }
} 
```

---
    
modify the `GreetingResource` class, injecting the `GreetingService` and adding a new method:

```java
@Inject
GreetingService greetingService;

@GET
@Produces(MediaType.TEXT_PLAIN)
@Path("/{name}")
public String hello(@PathParam("name") String name) { 
    return greetingService.greeting(name);
}
```

Note:
Do not use private modifier on injected fields - Quarkus must use reflection fallback to access private members (which entails bigger native executable) or use private fields and package-private constructor. 

---

Create a new property in `application.properties`
```properties
greeting=Good morning {0}!
```
<!-- .element: style="text-align:center" -->

---

Modify the `GreetingService` to use new property:

```java
@ConfigProperty(name = "greeting")
private String greeting;

String greeting(String name){
    return MessageFormat.format(greeting, name);
}
```

---

Use _package-private_ modifier: 

```java
@ConfigProperty(name = "greeting")
String greeting;
```

---

### Packaging Quarkus application

```commandline
./mvnw package
```
<!-- .element: style="text-align:center" -->

---
 
#### Produces two jars:

* `movies-app-1.0-SNAPSHOT.jar`
   * containing just the classes and resources of the projects
   
* `movies-app-1.0-SNAPSHOT-runner.jar`
   * an executable jar with the dependencies copied to `target/lib`

```commandline
java -jar target/movies-app-1.0-SNAPSHOT-runner.jar
```
<!-- .element: style="text-align:center" -->

Note:
* first - regular artifact produced by the Maven
* `*-runner.jar` is not a fat jar, it's a thin jar
    * separates concerns of libraries and our own code
    * Docker - as long as dependencies are not changed, the library layer is reused and built only once 

---

### Using Hibernate ORM and JPA

```commandline
./mvnw quarkus:add-extension \
    -Dextensions="quarkus-hibernate-orm,quarkus-jdbc-h2,quarkus-smallrye-openapi,quarkus-resteasy-jsonb"
```
<!-- .element: style="text-align:center" -->

---

Add configuration to `application.properties`:

```properties
quarkus.datasource.url=jdbc:h2:mem:test;Mode=Oracle;
quarkus.datasource.driver=org.h2.Driver
quarkus.datasource.username=username-default
quarkus.hibernate-orm.dialect=org.hibernate.dialect.Oracle10gDialect
quarkus.hibernate-orm.database.generation=create
quarkus.hibernate-orm.log.sql=true
```

---

Add lombok dependency:

```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <version>1.18.8</version>
</dependency>
```

---

Create an entity class: 

```java
@Data
@Entity
public class Movie {

    @Id 
    @GeneratedValue
    private Long id;
    private String title;
    private String genre;
    private int year;
}
```

---

Create a service:

```java
@ApplicationScoped
public class MovieService {
    @Inject
    EntityManager em;

    List<Movie> all() {
        return em.createQuery("SELECT e FROM Movie e").getResultList();
    }

    @Transactional
    void save(Movie movie) {
        em.persist(movie);
    }
}
```

---

Create an `import.sql` script in resources:

```sql
insert into movie(id, title, genre, year) values
    (hibernate_sequence.nextVal, 'The Shawshank Redemption', 'Drama', 1994),
    (hibernate_sequence.nextVal, 'One Flew Over the Cuckoo''s Nest', 'Drama', 1975),
    (hibernate_sequence.nextVal, 'The Lord of the Rings: The Fellowship of the Ring', 'Adventure, Drama, Fantasy', 2001),
    (hibernate_sequence.nextVal, '12 Angry Men', 'Drama', 1957);
```

---

Create a rest endpoint:

```java
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieService movieService;

    @GET
    public List<Movie> movies() {
        return movieService.all();
    }

    @POST
    public void save(Movie movie) {
        movieService.save(movie);
    }
}
```

---

SwaggerUI

---

Switch to Panache

```commandline
./mvnw quarkus:add-extension \
    -Dextension="quarkus-hibernate-orm-panache"
```
<!-- .element: style="text-align:center" -->

---

* Extend entity class with `PanacheEntity`

* Remove `id` field

* Make use of `PanacheEntity` static methods

Note:
"IDs are often a touchy subject, and not everyone’s up for letting them handled by the framework" - we can define own ID strategy by extending `PanacheEntityBase` instead of `PanacheEntity` and declaring public ID field.

---

```java
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Movie extends PanacheEntity {

    static final String YEAR = "year";
    static final Sort SORT_BY_YEAR = Sort.by(YEAR);

    private String title;
    private String genre;
    private int year;

    public static List<Movie> listAll() {
        return listAll(SORT_BY_YEAR);
    }

    static List<Movie> listByYear(int year) {
        return list(YEAR, year);
    }
}
```

---

```commandline
rm MovieService.java
```
<!-- .element: style="text-align:center" -->

---

Update `MovieResource` class:

```java
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @GET
    public List<Movie> movies(@QueryParam(YEAR) Integer year) {
        return Optional.ofNullable(year)
            .map(Movie::listByYear)
            .orElseGet(Movie::listAll);
    }

    @POST
    @Transactional
    public void save(Movie movie) {
        movie.persist();
    }
}
```

---

MicroProfile Rest Client

```commandline
./mvnw quarkus:add-extension -Dextension="rest-client"
```
<!-- .element: style="text-align:center" -->

---

Create a POJO class:

```java
@Data
public class Rating {

    double imdbRating;
    String imdbVotes;
    @JsonbProperty("Title")
    String title;
}
```

---

Create an interface

```java
@Path("/")
@RegisterRestClient
public interface RatingService {

    @GET
    @Produces("application/json")
    Rating getByTitle(@QueryParam("t") String title);
}
```

Note:
`@RegisterRestClient` allows Quarkus to know that this interface is meant to be available for CDI injection as a REST Client - not needed?

---

Create the configuration

```properties
com.example.RatingService/mp-rest/url=http://www.omdbapi.com/?apikey=3ea3f354
```

---

Inject REST client and add a new endpoint

```java
    @Inject
    @RestClient
    RatingService ratingService;

    @GET
    @Path("/{id}/rating")
    public Rating rating(@PathParam("id") Long id) {
        String title = Movie.<Movie>findById(id).getTitle();
        return ratingService.getByTitle(title);
    }
```

---

MicroProfile Fault Tolerance

```commandline
./mvnw quarkus:add-extension -Dextension="smallrye-fault-tolerance"
```
<!-- .element: style="text-align:center" -->

---

Let's suppose our RatingService may fail...

```java
@Slf4j
public class MovieResource {
    //...    
    private AtomicLong counter = new AtomicLong(0);
    //...
    public Rating rating(@PathParam("id") Long id) {
        Long invocationNumber = counter.getAndIncrement();
        maybeFail(invocationNumber);
        log.info("Rating invocation #{} succeeded", invocationNumber);
        //...
    }

    private void maybeFail(Long invocationNumber) {
        if (new Random().nextBoolean()) {
            log.error("Rating invocation #{} failed", invocationNumber);
            throw new RuntimeException("Could not fetch data.");
        }
    }
}
```

---

`@Retry`

```java
@Retry(maxRetries = 4)
public Rating rating(@PathParam("id") Long id) {
    //...
}
```

Note:

Retry options:
* maxRetries 
* retryOn / abortOn
* maxDuration, durationUnit 
* delay, delayUnit
* jitter, jitterDelayUnit

---

Create a recommendation service

```java
@ApplicationScoped
class RecommendationService {

    List<Movie> getRecommendations(Long id) {
        List<Movie> recommendations = Movie.list("id != ?1", id);
        Collections.shuffle(recommendations);
        return recommendations.subList(0, 2);
    }
}
```

---

Use it in `MovieResource` 

```java
    @Inject
    RecommendationService recommendationService;
    
    @GET
    @Path("/{id}/recommendations")
    @Timeout(250)
    public List<Movie> recommendations(@PathParam("id") Long id) {
        long started = System.currentTimeMillis();
        long invocationNumber = counter.getAndIncrement();

        try {
            randomDelay();
            log.info("Recommendations invocation #{} succeeded", invocationNumber);
            return recommendationService.getRecommendations(id);
        } catch (InterruptedException e) {
            log.error("Recommendations invocation #{} timed out after {} ms", invocationNumber,
                System.currentTimeMillis() - started);
            return null;
        }
    }

    private void randomDelay() throws InterruptedException {
        Thread.sleep(new Random().nextInt(500));
    }
```

---

Add a fallback method

```java

    @Fallback(fallbackMethod = "fallbackRecommendations")
    public List<Movie> recommendations(@PathParam("id") Long id) {
        //...
    }

    private List<Movie> fallbackRecommendations(Long id) {
        log.info("Recommendation fallback...");
        return List.of(Movie.findById(id != 1L ? 1L : 2L));
    }
```

---

MicroProfile Health

```commandline
./mvnw quarkus:add-extension -Dextension="health"
```
<!-- .element: style="text-align:center" -->

---

`/health/live` - app is up and running

`/health/ready` - app is ready to serve requests

`/health` - accumulating both 

Note:
* liveness - should the application be restarted 
* readiness - does it makes sense to contact the application with requests
* each extension can register its own health checks (e.g. `quarkus-agroal`)
* extension health check can be disabled via property `quarkus.health.extensions.enabled`

---

`@Liveness` / `@Readiness`

```java
@Liveness
@ApplicationScoped
public class MovieAppHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("MovieApp is up");
    }
}
```

---

MicroProfile Metrics

```commandline
./mvnw quarkus:add-extension -Dextension="metrics"
```
<!-- .element: style="text-align:center" -->

<small>(in our case it's already present - `smallrye-fault-tolerance` dependency)</small>

---

`/metrics/application`

`/metrics/base` 

`/metrics/vendor` 

`/metrics`

---

```java
@GET
@Counted(name = "getMoviesCounter", description = "How many times movies endpoint has been called")
@Timed(name = "getMoviesTimer", description = "A measure of how long it takes to get movies", unit = MILLISECONDS)
public List<Movie> movies(@QueryParam(YEAR) Integer year) {
    // ...
}
```

---

### Summary

* We've created a simple app exposing REST endpoints via RESTEasy
* We've connected to H2 database using JPA / Hibernate (Panache)
* We've used MicroProfile Rest Client to fetch movies ratings

---

* We've used MicroProfile Fault Tolerance to make app resilient
* We've added health check and metrics to easily monitor our application