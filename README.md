# Flash Job, a sample play app

This app is a two sided, time driven job board : publish your offer / criteria, they will be available one week !

It main purpose is to implement best practices for real-world play 2 apps.

## Requirements

This project works with :

- Java 8
- Scala 2.11.7
- sbt 0.13.11
- Play framework 2.5.6
- MongoDB 3.0.2

## Configuration

TODO

## Running the Application

### Running tests

TODO

### Running localy

- start mongo
- `$ sbt run`

### Deploying in prod

- Setup a heroku instance
- add addon for MongoDB (mLab)
- publish the app

## Continuous integration

TODO

## Architecture

### Scala code

- `global` package is a "personal library" & project agnostic
- `com.flashjob` package for app code
    - `common` package is generic code shared throughout the project (such as Config...)
    - `domain` package is the core logic of the project. It should be framework agnostic as mush as possible
        - `models` package contains all data definition
        - `services` package contains project logic
    - `infrastructure` package is used for code interfacing with external source (database, webservices...)
    - `controllers`
    - `views`

## Interesting libs

- [doobie](https://github.com/tpolecat/doobie) pure functional JDBC layer for Scala

## TODO

- DI with macwire
- TDD
    - embed mongo (test port)
    - run tests at compile
- i18n
- rest api
    - documentation (api blueprint)
    - auth token
    - rate limiting
- user auth/admin
    - https://github.com/mohiva/play-silhouette-persistence-reactivemongo
- living documentation (https://leanpub.com/livingdocumentation)
- articles
    - compile time DI
        - ApplicationLoader
        - Test controllers
    - setup sbt-buildinfo (with git hash)
    - setup reactivemong
- talks
    - type all the things !

## Credits

- https://github.com/MfgLabs/PSUG for most best practices
- https://github.com/playframework/play-scala-compile-di-with-tests for controller tests
