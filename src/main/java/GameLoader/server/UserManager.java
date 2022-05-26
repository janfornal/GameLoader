package GameLoader.server;

import GameLoader.common.Connection;
import static GameLoader.common.Messages.*;

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

    public synchronized void processAuthorizationAttemptMessage(AuthorizationAttemptMessage msg, Connection c) {
        if (c.isAuthorized()) {
            c.sendError("You are already authorized");
            return;
        }

        String name = msg.name();
        String password = server.passwordManager.hash(name, msg.password());
        String expectedPassword = server.dataManager.getPlayerPassword(name);

        if (expectedPassword == null) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("This account does not exist"));
            return;
        }

        if (!expectedPassword.equals(password)) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("Password does not match"));
            return;
        }

        if (server.dataManager.getPlayerId(name) == null) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("Your account does not exist"));
            return;
        }

        if (connectionMap.containsKey(name)) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("You are already connected, disconnecting..."));
            getConnection(name).close();
            return;
        }

        c.authorize(name);
        connectionMap.put(name, c);
        c.sendMessage(new SuccessfulAuthorizationMessage());
//        c.sendMessages(new SuccessfulAuthorizationMessage(),
//                new StartGameMessage("Paper soccer", "Small", 1, new PlayerInfo("1", 1), new PlayerInfo("2", 2)));
    }

    public synchronized void processRegistrationAttemptMessage(RegistrationAttemptMessage msg, Connection c) {
        if (c.isAuthorized()) {
            c.sendError("You are already authorized");
            return;
        }

        String name = msg.name();
        String password = server.passwordManager.hash(name, msg.password());

        if (server.dataManager.playerExists(name)) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("User with this name already exists"));
            return;
        }

        if (server.dataManager.registerPlayer(name, password) == null) {
            c.sendMessage(new UnsuccessfulAuthorizationMessage("Unsuccessful registration"));
            return;
        }

        c.authorize(name);
        connectionMap.put(name, c);
        c.sendMessage(new SuccessfulAuthorizationMessage());
    }

    public void sendMessageTo(AnyMessage msg, String... to) {
        for (String name : to) {
            Connection c = getConnection(name);
            if (c == null)
                continue;
            c.sendMessage(msg);
        }
    }

    public void sendErrorTo(String cause, String... to) {
        sendMessageTo(new ErrorMessage(cause), to);
    }
}
