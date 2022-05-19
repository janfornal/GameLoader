package GameLoader.server;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import GameLoader.common.Service;

import java.net.Socket;
import java.util.*;

/**
 * This class is thread-safe
 */
public class ConnectionManager {
    private final Server server;
    public ConnectionManager(Server s) {
        server = s;
    }

    private final Map<String, Connection> connectionMap = new HashMap<>();
    private final Set<Connection> connectionSet = new HashSet<>();

    public synchronized void createConnection(Socket s) {
        connectionSet.add(new Connection(server, s));
    }

    public synchronized void unregisterConnection(Connection c) {
        connectionMap.remove(c.getName());
        connectionSet.remove(c);
    }

    public synchronized void closeAllConnections() {
        for (Connection c : connectionSet)
            server.execNormal.execute(c::close);
    }

    public Connection getConnection(String name) {
        return connectionMap.get(name);
    }

    public synchronized void processAuthorizationMessage(Message.Authorization msg, Connection c) {
        if (c.isAuthorized())
            c.sendError("You are already authorized");

        String name = msg.name();

        if (name == null || name.equals("")) {
            c.sendError("Name should not be empty");
            return;
        }

        if (server.dataManager.getPlayerId(name) == null) { // FIXME make this nicer
            c.sendError("Your account does not exist");
            return;
        }

        if (connectionMap.containsKey(name)) {
            c.sendError("You are already connected");
            return;
        }

        // TODO check password

        c.sendMessage(new Message.Authorization(""));
        connectionMap.put(name, c);
        c.authorize(name);
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
