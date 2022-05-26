package GameLoader.server;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import GameLoader.common.PlayerInfo;

import java.net.Socket;
import java.util.*;

/**
 * This class is thread-safe
 */
public class UserManager {
    private final Server server;
    public UserManager(Server s) {
        server = s;
    }

    private final Map<String, Connection> connectionMap = new HashMap<>();
    private final Set<Connection> connectionSet = new HashSet<>();

    public synchronized void createConnection(Socket s) {
        connectionSet.add(new Connection(server, s));
    }

    public synchronized void reportConnectionClosed(Connection c) {
        connectionSet.remove(c);
        if (!c.isAuthorized())
            return;

        connectionMap.remove(c.getName());
        server.gameManager.reportConnectionClosed(c);
    }

    public synchronized void closeAllConnections() {
        for (Connection c : connectionSet)
            server.execNormal.execute(c::close);
    }

    public Connection getConnection(String name) {
        return connectionMap.get(name);
    }

    public synchronized void processAuthorizationAttemptMessage(Message.AuthorizationAttempt msg, Connection c) {
        if (c.isAuthorized()) {
            c.sendError("You are already authorized");
            return;
        }

        String name = msg.name();
        String password = server.passwordManager.hash(name, msg.password());
        String expectedPassword = server.dataManager.getPlayerPassword(name);

        if (expectedPassword == null) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("This account does not exist"));
            return;
        }

        if (!expectedPassword.equals(password)) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("Password does not match"));
            return;
        }

        if (server.dataManager.getPlayerId(name) == null) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("Your account does not exist"));
            return;
        }

        if (connectionMap.containsKey(name)) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("You are already connected, disconnecting..."));
            getConnection(name).close();
            return;
        }

        c.authorize(name);
        connectionMap.put(name, c);
        c.sendMessage(new Message.SuccessfulAuthorization());
//        c.sendMessages(new Message.SuccessfulAuthorization(),
//                new Message.StartGame("Paper soccer", "Small", 1, new PlayerInfo("1", 1), new PlayerInfo("2", 2)));
    }

    public synchronized void processRegistrationAttemptMessage(Message.RegistrationAttempt msg, Connection c) {
        if (c.isAuthorized()) {
            c.sendError("You are already authorized");
            return;
        }

        String name = msg.name();
        String password = server.passwordManager.hash(name, msg.password());

        if (server.dataManager.playerExists(name)) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("User with this name already exists"));
            return;
        }

        if (server.dataManager.registerPlayer(name, password) == null) {
            c.sendMessage(new Message.UnsuccessfulAuthorization("Unsuccessful registration"));
            return;
        }

        c.authorize(name);
        connectionMap.put(name, c);
        c.sendMessage(new Message.SuccessfulAuthorization());
    }

    public void sendMessageTo(Message.Any msg, String... to) {
        for (String name : to) {
            Connection c = getConnection(name);
            if (c == null)
                continue;
            c.sendMessage(msg);
        }
    }

    public void sendErrorTo(String cause, String... to) {
        sendMessageTo(new Message.Error(cause), to);
    }
}
