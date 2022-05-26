# GameLoader

Client and server apps allowing users to play simple games online, made as an assignment project for OOP course 2021/22.

## Goals

- [x] working client / server connection (29.04)
- [x] first working game with GUI (01.05)
- [x] first online game played (02.05)
- [x] dots and boxes game (03.05)

#### First presentation (05.05)

- [x] in-game chat (16.05)
- [x] database integration (user accounts, elo system) (19.05)
- [x] tic-tac-toe game with multiple settings (24.05)
- [x] paper soccer game (25.05)

#### Second presentation (26.05)

- [ ] game history
- [ ] rejoining games after disconnection
- [ ] loading games as plugins

## Requirements

- Java 17
- OpenJFX 17
- Maven 3 as the build system
- JUnit 4 for unit tests

## Compiling

    mvn clean package                                                               # Compile everything

## Basic usage

    java -jar target/GameLoader-1-shaded.jar <ip> <port>                            # Run client
    java -cp target/GameLoader-1-shaded.jar GameLoader/server/ServerRun <port>      # Run server

If not specified ``port`` defaults to ``6666``, and ``ip`` defaults to ``localhost``.
