#### Windows commands

Windows versions of commands used on [slides](slides.md).

1. Create a new Quarkus project

    ```commandline
    mvn io.quarkus:quarkus-maven-plugin:0.20.0:create ^
        -DprojectGroupId=com.example ^
        -DprojectArtifactId=movies-app ^
        -DclassName="com.example.GreetingResource" ^
        -Dpath="/hello"
    ```

1. Run application in dev mode

    ```commandline
    mvnw.cmd compile quarkus:dev
    ```

1. Package application

    ```commandline
    mvnw.cmd package
    ``` 

1. Run application from JAR

    ```commandline
    java -jar target/movies-app-1.0-SNAPSHOT-runner.jar
    ```

1. Add Hibernate, H2, OpenAPI, Jakarta JSON-B extensions

    ```commandline
        mvnw.cmd quarkus:add-extension ^
            -Dextensions="quarkus-hibernate-orm,quarkus-jdbc-h2,quarkus-smallrye-openapi,quarkus-resteasy-jsonb"
    ```

1. Add Panache extension

    ```commandline
    mvnw.cmd quarkus:add-extension ^
        -Dextension="quarkus-hibernate-orm-panache"
    ```    

1. Add SmallRye REST Client extension

    ```commandline
    mvnw.cmd quarkus:add-extension -Dextension="rest-client"
    ```

1. Add SmallRye Fault Tolerance extension

    ```commandline
    mvnw.cmd quarkus:add-extension -Dextension="smallrye-fault-tolerance"
    ```
