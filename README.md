# GameLoader

Client and server apps allowing users to play simple games online, made as assignment project for OOP course 2021/22.

## Goals

- [x] working client / server connection (30.04)
- [x] first working game with GUI (01.05) 
- [x] first online game played (02.05)
- [ ] Dots And Boxes game

#### First presentation (05.05)
- [ ] Tic Tac Toe game with multiple settings
- [ ] elo system
- [ ] rejoining games after disconnection
- [ ] loading games as plugins

## Compiling

    mvn clean package                                                               # Compile everything

## Basic usage

    java -jar target/GameLoader-1-shaded.jar <ip> <port>                            # Run client
    java -cp target/GameLoader-1-shaded.jar GameLoader/server/ServerRun <port>      # Run server

If not specified ``ip`` defaults to ``localhost``, and ``port`` defaults to ``6666``.