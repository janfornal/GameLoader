package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.messages.Message;

public class Server extends AbstractService {
    public Server(int port) {

    }
    public Server() {
        this(Connection.defaultPort);
    }

    @Override
    public void processMessage(Message m) {

    }

    @Override
    public void reportGameEnded(Game.GameInstance gm) {

    }

    class ListeningConnection implements Runnable {

        @Override
        public void run() {

        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
