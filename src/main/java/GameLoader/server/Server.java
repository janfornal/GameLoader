package GameLoader.server;

import GameLoader.common.Service;
import GameLoader.common.Connection;
import GameLoader.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements Service {
    private final int port;
    private boolean closed = false;
    private ServerSocket serverSocket;

    public final GameManager gameManager = new GameManager(this);
    public final UserManager userManager = new UserManager(this);
    public final GameTypeManager gameTypeManager = new GameTypeManager(this);
    public final DataManager dataManager = new DatabaseManager(this);
    public final EloManager eloManager = new SimpleEloManager(this);

    public Server() {
        this(Connection.defaultPort);
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
    public void processMessage(Message.Any msg, Connection c) {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(c);

        if (msg instanceof Message.Error m) {
            Service.ERROR_STREAM.println(m);
            return;
        }
        if (msg instanceof Message.AuthorizationAttempt m) {
            userManager.processAuthorizationAttemptMessage(m, c);
            return;
        }
        if (msg instanceof Message.RegistrationAttempt m) {
            userManager.processRegistrationAttemptMessage(m, c);
            return;
        }
        if (!c.isAuthorized()) {
            c.sendError("You are not authorized");
            return;
        }
        if (msg instanceof Message.Move m) {
            gameManager.processMoveMessage(m, c);
            return;
        }
        if (msg instanceof Message.CreateRoom m) {
            gameManager.processCreateRoomMessage(m, c);
            return;
        }
        if (msg instanceof Message.GetRoomList m) {
            gameManager.processGetRoomListMessage(m, c);
            return;
        }
        if (msg instanceof Message.JoinRoom m) {
            gameManager.processJoinRoomMessage(m, c);
            return;
        }
        if (msg instanceof Message.GetGameList m) {
            gameTypeManager.processGetGameListMessage(m, c);
            return;
        }
        if (msg instanceof Message.ChatMessage m) {
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
