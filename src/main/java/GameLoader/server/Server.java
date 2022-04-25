package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Message;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements AbstractService {
    private final int port;

    public int getPort() {
        return port;
    }

    public Server() {
        this(Connection.defaultPort);
    }

    public Server(int port) {
        this.port = port;
        execNormal.execute(() -> {
            try {
                final ServerSocket serverSocket = new ServerSocket(port);
                while (!serverSocket.isClosed())
                    new Connection(Server.this, serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void processMessage(Message.Any message, Connection connection) {
        System.err.println(message);
    }

    @Override
    public void reportGameEnded(Game game) {

    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public static void main(String[] args) {
        new Server();
    }
}
