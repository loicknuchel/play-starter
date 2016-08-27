# Flash Job

A two sided, time driven job board : publish your offer / criteria, they will be available one week !

## Requirements

This project require Java 8, Scala 2.11.7, sbt & mongo.

## Configuration

TODO

## Running the Application

### Running tests

TODO

### Running localy

- start mongo
- $ sbt run

### Deploying in prod

- Setup a heroku instance
- add addon for MongoDB (mLab)
- publish the app

## Continuous integration

TODO

## Architecture

### Scala code

- `global` package is a "personal library" & project agnostic
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

- api documentation
- i18n
- user admin
- living documentation (https://leanpub.com/livingdocumentation)
- articles
    - setup sbt-buildinfo (with git hash)
    - setup reactivemongo

## Credits

Inspiration taken from https://github.com/MfgLabs/PSUG
