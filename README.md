# Flash Job, a sample play app

This app is a two sided, time driven job board : publish your offer / criteria, they will be available one week !

It main purpose is to implement best practices for real-world play 2 apps.

- compile time DI
- typing everywhere
- testing (unit, integration)
- normalized REST API
    - basic crud
    - search and pagination
    - nice error handling
    - generated documentation
- application status with build time, git hash & strategic resources status

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

## Documentation

Api documentation is generated using [apiblueprint](https://apiblueprint.org/) standard and [aglio](https://github.com/danielgtaylor/aglio) generator.

To re-generate it you should install `sudo npm install -g aglio` and run :

```
aglio -i docs/api.apib -o public/docs/api.html --theme-variables streak --theme-template triple --theme-full-width
```

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

- [monix](https://monix.io/) asynchronous Programming for Scala
- [doobie](https://github.com/tpolecat/doobie) pure functional JDBC layer for Scala

## TODO

- DI with macwire
- TDD
    - embed mongo (test port)
    - run tests at compile
    - property based testing (https://www.scalacheck.org/)
- i18n
    - app text
    - date format (display & forms)
- great log system
- rest api
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
    - setup reactivemongo
- talks
    - type all the things !

## Credits

- https://github.com/MfgLabs/PSUG for most best practices
- https://github.com/playframework/play-scala-compile-di-with-tests for controller tests
