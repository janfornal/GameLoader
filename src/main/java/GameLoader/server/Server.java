package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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

    private final Map<String, Connection> connectionMap = new HashMap<>();

    @Override
    public void processMessage(Message.Any msg, Connection conn) {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(conn);

        System.err.println(msg);

        if (msg instanceof Message.Authorization m) {
            processAuthorizationMessage(m, conn);
            return;
        }
        if (!conn.isAuthorized()) {
            conn.sendError("You are not authorized");
            return;
        }

        conn.sendError("Message not recognized");
    }

    private void processAuthorizationMessage(Message.Authorization msg, Connection conn) {
        if (conn.isAuthorized())
            conn.sendError("You are already authorized");
        String pn = msg.name();

        synchronized (connectionMap) {
            if (connectionMap.containsKey(pn) || new Random().nextInt(5) == 0)
                conn.sendError("Unsuccessful authorization");
            else {
                // conn.sendMessage(); send success info?
                connectionMap.put(pn, conn);
                conn.authorize(pn);
            }
        }
    }

    @Override
    public void reportGameEnded(Game game) {

    }

    @Override
    public void reportConnectionClosed(Connection connection) {
        if (!connection.isAuthorized())
            return;


    }



    public static void main(String[] args) {

    }
}
