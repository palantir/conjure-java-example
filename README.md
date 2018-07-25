conjure-java-example
===================
A simple recipe application that uses dropwizard framework and conjure bindings.

Overview
--------
This example project uses the following tools and libraries, please consult their respective documentation for more information.

* [conjure](https://github.com/palantir/conjure) - IDL for defining APIs once and generating client/server interfaces in different languages.
* [gradle](https://gradle.org/) - a highly flexible build tool. Some of the gradle plugins applied are:
     *  [gradle-conjure](https://github.com/palantir/gradle-conjure) - a gradle plugin that contains tasks to generate conjure bindings.
     *  [gradle-baseline](https://github.com/palantir/gradle-baseline) - a gradle plugin for configuring code quality tools in builds and projects.
* [dropwizard](https://www.dropwizard.io/1.3.5/docs/) - a simple library that for building web services

Start Developing
----------------
Run one of the following commands:

* `./gradlew tasks` for tasks available in this project.
* `./gradlew idea` for IntelliJ
* `./gradlew eclipse` for Eclipse
* `./gradlew run` for running the server or use IDE to debug it

Project Structure
-----------------

* `recipe-example-api` - a sub-project that defines recipe-example APIs in Conjure and generates both java and typescript bindings.

    This is what the api project looks like:
    ```
    ├── recipe-example-api
    │   ├── build.gradle
    │   ├── recipe-example-api-jersey
    │   ├── recipe-example-api-objects
    │   ├── recipe-example-api-typescript
    │   └── src
    │       └── main
    │           └── conjure
    │               └── recipe-example-api.yml
    ```
    * build.gradle - a gradle script that 
        1. configures sub-projects with needed dependencies to generate java bindings. e.g. `recipe-example-api-jersey`
        2. configures `publishTypescript` task to generate `.npmrc` in the generated root folder, `recipe-example-api-typescript/src` for publishing the generated npm module.
        3. and modifies the `conjure` extension to specify the package name under which the npm module will be published.
    * recipe-example-api-jersey - the sub-project where all generated [service interfaces](https://github.com/palantir/conjure-java-example/blob/0.1.1/example-api/src/main/conjure/example-api.yml#L39) live.
    * recipe-example-api-objects - the sub-project where all generated [object classes](https://github.com/palantir/conjure-java-example/blob/0.1.1/example-api/src/main/conjure/example-api.yml#L4) live.
    * recipe-example-api-typescript - the sub-project where all generated typescript bindings live.
    * src/main/conjure - the conjure definition yml file where recipe APIs are defined.

* `recipe-example-server` - a dropwizard application project that uses conjure generated jersey binding for resource class implementation

    This is what the server project looks like:
    ```
    ├── recipe-example-server
    │   ├── build.gradle
    │   ├── src
    │   │   ├── main
    │   │   │   └── java
    │   │   │       └── com
    │   │   │           └── palantir
    │   │   │               └── conjure
    │   │   │                   └── examples
    │   │   │                       ├── RecipeBookApplication.java
    │   │   │                       ├── RecipeBookConfiguration.java
    │   │   │                       └── resources
    │   │   │                           └── RecipeBookResource.java
    │   │   └── test
    │   │       ├── java
    │   │       │   └── com
    │   │       │       └── palantir
    │   │       │           └── conjure
    │   │       │               └── examples
    │   │       │                   └── RecipeBookApplicationTest.java
    │   │       └── resources
    │   │           └── test.yml
    │   └── var
    │       └── conf
    │           └── recipes.yml
    ```
    * build.gradle - configures the project with needed dependencies and applies the gradle plugin, so we can run the server locally or in IDE.
    * src/main/java - source classes for the dropwizard application.
    * test/main/java - test source classes for simple integration tests. 
    * var/conf/recipes.yml - the dropwizard application configuration yml file

* build.gradle - the root level gradle script where a set of gradle plugins are configured, including [gradle-conjure](https://github.com/palantir/gradle-conjure).
