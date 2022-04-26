package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements AbstractService {
    private final int port;
    private final Object connectionLock = new Object();
    private final Map<String, Connection> connectionMap = new HashMap<>();
    private final Set<Connection> connectionSet = new HashSet<>();
    private boolean closed = false;
    private ServerSocket serverSocket;

    public Server() {
        this(Connection.defaultPort);
    }

    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            closed = true;
            return;
        }

        execNormal.execute(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket received = serverSocket.accept();
                    synchronized (connectionLock) {
                        connectionSet.add(new Connection(Server.this, received));
                    }
                }
            } catch (IOException e) {
                if (!closed) {
                    e.printStackTrace();
                    close();
                }
            }
        });
    }

    public static void main(String[] args) {

    }

    public boolean isClosed() {
        return closed;
    }

    public int getPort() {
        return port;
    }

    public void close() {
        if (closed)
            return;
        synchronized (connectionLock) {
            for (Connection c : connectionSet)
                c.close();
            closed = true;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processMessage(Message.Any msg, Connection conn) {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(conn);

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

        synchronized (connectionLock) {
            if (connectionMap.containsKey(pn))
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
        synchronized (connectionLock) {
            connectionSet.remove(connection);
            connectionMap.remove(connection.getName());
        }
    }
}
