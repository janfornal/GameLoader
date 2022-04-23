package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.messages.AuthorizationMessage;
import GameLoader.common.messages.ErrorMessage;
import GameLoader.common.messages.Message;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends AbstractService {
    private int port;

    public Server(int p) {
        port = p;
        execNormal.execute(new ListeningConnection());
    }

    public Server() {
        this(Connection.defaultPort);
    }

    public static void main(String[] args) {
        new Server();
    }

    @Override
    public void processMessage(Message m) {
        System.out.println(m);

        if (!m.c.isAuthorized()) {
            if (m instanceof AuthorizationMessage auth) {
                m.c.authorize(auth.name);
                m.c.sendMessage(new ErrorMessage("Successful authorization " + auth.name));
            } else {
                m.c.sendMessage(new ErrorMessage("Client is not authorized"));
            }
            return;
        }

        m.c.sendMessage(new ErrorMessage("Unrecognized message"));

        System.out.println(m);
    }

    @Override
    public void reportGameEnded(Game.GameInstance gm) {

    }

    class ListeningConnection implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    new Connection(serverSocket.accept());
                    System.err.println("Connection established");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
