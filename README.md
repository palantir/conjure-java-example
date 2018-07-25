# conjure-java-example
A small recipe application that demonstrates the simple usage of conjure tooling.

## Overview

#### Tools and Libraries

This example project uses the following tools and libraries, please consult their respective documentation for more information.

* [conjure](https://github.com/palantir/conjure) - IDL for defining APIs once and generating client/server interfaces in different languages.
    * [conjure-java-runtime](https://github.com/palantir/http-remoting/) - conjure libraries for HTTP&JSON-based RPC using Retrofit, Feign, OkHttp as clients and Jetty/Jersey as servers
    * [conjure-java](https://github.com/palantir/conjure-java) - conjure generator for java clients and servers 
    * [conjure-typescript](https://github.com/palantir/conjure-java) - conjure generator for typescript clients
* [gradle](https://gradle.org/) - a highly flexible build tool. Some of the gradle plugins applied are:
     *  [gradle-conjure](https://github.com/palantir/gradle-conjure) - a gradle plugin that contains tasks to generate conjure bindings.
     *  [gradle-baseline](https://github.com/palantir/gradle-baseline) - a gradle plugin for configuring code quality tools in builds and projects.
* [dropwizard](https://www.dropwizard.io/1.3.5/docs/) - a simple framework for building web services

#### Project Structure

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
    * src/main/conjure - the conjure definition yml file where recipe APIs are defined, please refer to [specification.md](https://github.com/palantir/conjure/blob/develop/docs/specification.md) for more details.

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
    * src/main/java - source classes for the dropwizard application. e.g. RecipeBookResource.java class `implements` the generated Jersey interface.
    * test/main/java - test source classes for simple integration tests that uses generated jersey interface for client interaction.
    * var/conf/recipes.yml - the dropwizard application configuration yml file

* build.gradle - the root level gradle script where a set of gradle plugins are configured, including [gradle-conjure](https://github.com/palantir/gradle-conjure).
* settings.gradle - the gradle settings file where all sub projects are configured.
* versions.props - a property file of the [version recommender plugin](https://github.com/nebula-plugins/nebula-dependency-recommender-plugin) with which we can specify versions of project dependencies, including conjure generators.

## Development

#### Useful Gradle Commands:

* `./gradlew tasks` for tasks available in this project.
* `./gradlew idea` for IntelliJ
* `./gradlew eclipse` for Eclipse
* `./gradlew run` for running the server or use IDE to debug it

#### Generate New or Modify Existing APIs

##### Modify existing APIs
To modify the existing bindings in this project:
1. Make changes to the `recipe-example-api/src/main/conjure/recipe-api.yml` file
2. Run `./gradlew compileConjure` or a more specific task such as `./gradlew compileConjureObjects`, to check if the changes compile
3. Or run `./gradlew idea` or `./gradlew eclipse` to update the bindings for your IDE

##### Generate new binding for a different language
To generate bindings for a new language. Note that currently `gradle-conjure` plugin only supports generation of java, typescript, and python bindings.
1. Add a new sub project under `recipe-example-api` by modifying the `settings.gradle` file. 
    ```diff
    ...
    include 'example-api:example-api-typescript'
    + include 'example-api:example-api-python'
    ```
2. Optional: use the gradle script `configure` closure in `recipe-example-api/build.gradle` to configure project specific settings for the new sub project.
3. Specify conjure python dependency versions in versions.props
   ```diff
   + com.palantir.conjure.python:* = 3.4.0
   ```
4. run `./gradlew compileConjurePython` to generate new bindings for python.

##### Generate Java retrofit interfaces
Similar to how we add the conjure generation for python above, we can add a new project to generate java retrofit interfaces
1. add a new sub project under `recipe-example-api` by modifying the `settings.gradle` file. 
    ```diff
    ...
    include 'example-api:example-api-typescript'
    + include 'recipe-example-api:recipe-example-api-retrofit'
    ```
2. Optional: use the gradle script `configure` closure in `recipe-example-api/build.gradle` to configure project specific settings for the new sub project. 
3. run `./gradlew compileConjureRetrofit` to generate new bindings for python.
