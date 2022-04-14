package GameLoader.server;

import GameLoader.common.ConnectionHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class GameServer {
    private final ServerSocket serverSocket;
    public GameServer() throws IOException {
        serverSocket = new ServerSocket(ConnectionHandler.defaultPort);

        while (true) {
            Socket s = serverSocket.accept();
            new Thread(new ConnectedClient(new ConnectionHandler(s))).start();
        }
    }

    private static class ConnectedClient implements Runnable {
        private final ConnectionHandler connectionHandler;
        public ConnectedClient(ConnectionHandler ch) {
            Objects.requireNonNull(ch);
            connectionHandler = ch;
        }

        @Override
        public void run() {
            while (true) {
                String s = connectionHandler.in.poll();
                if (s == null)
                    continue;
                connectionHandler.out.add(s + "-echoed");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GameServer gameServer = new GameServer();
    }
}
