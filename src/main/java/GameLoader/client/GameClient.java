package GameLoader.client;

import GameLoader.common.ConnectionHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    public GameClient() throws IOException {
        Socket s = new Socket(ConnectionHandler.defaultIp, ConnectionHandler.defaultPort);
        ConnectionHandler connectionHandler = new ConnectionHandler(s);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String str;
            while ((str = connectionHandler.in.poll()) != null) {
                System.out.println(str);
            }
            connectionHandler.out.add(scanner.nextLine());
        }
    }

    public static void main(String[] args) throws IOException {
        GameClient gameClient = new GameClient();
    }

}
