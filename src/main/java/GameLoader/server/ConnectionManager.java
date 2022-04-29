package GameLoader.server;

import GameLoader.common.Connection;
import GameLoader.common.Message;

import java.net.Socket;
import java.util.*;

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

    public Connection getConnection(String p) {
        return connectionMap.get(p);
    }

    public /* unsynchronized */ void processAuthorizationMessage(Message.Authorization msg, Connection c) {
        if (c.isAuthorized())
            c.sendError("You are already authorized");
        String pn = msg.name();

        synchronized (this) {
            if (connectionMap.containsKey(pn))
                c.sendError("Unsuccessful authorization");
            else {
                // conn.sendMessage(); send success info?
                connectionMap.put(pn, c);
                c.authorize(pn);
            }
        }
    }

    public void sendMessageTo(Message.Any msg, String... to) {
        for (String str : to) {
            Connection c = getConnection(str);
            if (c == null)
                continue;
            c.sendMessage(msg);
        }
    }
}
