package GameLoader.server;

import GameLoader.common.*;

import static GameLoader.common.Messages.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements Service {
    private final int port;
    private boolean closed = false;
    private ServerSocket serverSocket;

    public final GameManager gameManager = new GameManager(this);
    public final DataManager dataManager = new DatabaseManager(this, new DatabaseConnectionFactory());
    public final UserManager userManager = new UserManager(this);
    public final EloManager eloManager = new SimpleEloManager();

    public Server() {
        this(Service.defaultPort);
    }

    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace(ERROR_STREAM);
            closed = true;
            return;
        }

        execNormal.execute(() -> {
            try {
                while (!serverSocket.isClosed())
                    userManager.createConnection(serverSocket.accept());
            } catch (IOException e) {
                if (!closed) {
                    e.printStackTrace(ERROR_STREAM);
                    close();
                }
            }
        });
    }

    public boolean isClosed() {
        return closed;
    }

    public int getPort() {
        return port;
    }

    public synchronized void close() {
        if (closed)
            return;
        closed = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace(ERROR_STREAM);
        }
        userManager.closeAllConnections();
    }

    @Override
    public void processMessage(AnyMessage msg, Connection c) {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(c);

        if (msg instanceof ErrorMessage m) {
            Service.ERROR_STREAM.println(m);
            return;
        }
        if (msg instanceof AuthorizationAttemptMessage m) {
            userManager.processAuthorizationAttemptMessage(m, c);
            return;
        }
        if (msg instanceof RegistrationAttemptMessage m) {
            userManager.processRegistrationAttemptMessage(m, c);
            return;
        }
        if (!c.isAuthorized()) {
            c.sendError("You are not authorized");
            return;
        }
        if (msg instanceof MoveMessage m) {
            gameManager.processMoveMessage(m, c);
            return;
        }
        if (msg instanceof CreateRoomMessage m) {
            gameManager.processCreateRoomMessage(m, c);
            return;
        }
        if (msg instanceof GetRoomListMessage m) {
            gameManager.processGetRoomListMessage(m, c);
            return;
        }
        if (msg instanceof JoinRoomMessage m) {
            gameManager.processJoinRoomMessage(m, c);
            return;
        }
        if (msg instanceof ChatMessage m) {
            gameManager.processChatMessage(m, c);
            return;
        }
        c.sendError("Message not recognized");
    }

    @Override
    public void reportConnectionClosed(Connection c) {
        userManager.reportConnectionClosed(c);
    }
}
