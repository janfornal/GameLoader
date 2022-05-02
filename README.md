# GameLoader

Client and server apps allowing users to play simple games online, made as an assignment project for OOP course 2021/22.

## Goals

- [x] working client / server connection (30.04)
- [x] first working game with GUI (01.05) 
- [x] first online game played (02.05)
- [ ] Dots and boxes game

#### First presentation (05.05)
- [ ] Tic Tac Toe game with multiple settings
- [ ] elo system persistent between server runs
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

## Legal

- Project code is licensed under the [MIT License](https://opensource.org/licenses/MIT)
- [OpenJFX](https://github.com/openjdk/jfx) is licensed under the [GNU General Public License version 2](https://opensource.org/licenses/GPL-2.0)
