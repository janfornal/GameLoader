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

    private final Map<Integer, Connection> connectionMap = new HashMap<>();
    private final Set<Connection> connectionSet = new HashSet<>();

    public synchronized void createConnection(Socket s) {
        connectionSet.add(new Connection(server, s));
    }

    public synchronized void unregisterConnection(Connection c) {
        connectionMap.remove(c.getId());
        connectionSet.remove(c);
    }

    public synchronized void closeAllConnections() {
        for (Connection c : connectionSet)
            server.execNormal.execute(c::close);
    }

    public Connection getConnection(int p) {
        return connectionMap.get(p);
    }

    public synchronized void processAuthorizationMessage(Message.Authorization msg, Connection c) {
        if (c.isAuthorized())
            c.sendError("You are already authorized");

        String name = msg.name();

        if (name == null || name.equals("")) {
            c.sendError("Name should not be empty");
            return;
        }

        int id = server.dataManager.getPlayerId(name);

        if (id == Service.INT_NULL) {
            c.sendError("Your account does not exist");
            return;
        }

        if (connectionMap.containsKey(id)) {
            c.sendError("You are already connected");
            return;
        }

        // TODO check password

        c.sendMessage(new Message.Authorization(""));
        connectionMap.put(id, c);
        c.authorize(id);
    }

    public void sendMessageTo(Message.Any msg, int... to) {
        for (int id : to) {
            Connection c = getConnection(id);
            if (c == null)
                continue;
            c.sendMessage(msg);
        }
    }

    public void sendErrorTo(String cause, int... to) {
        sendMessageTo(new Message.Error(cause), to);
    }
}
