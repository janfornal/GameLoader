package GameLoader.client;

import GameLoader.common.Connection;
import java.io.IOException;

public class ClientRun {
    public static void main(String[] args) throws IOException {
        String ip = args.length > 0 ? args[0] : Connection.defaultIP;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : Connection.defaultPort;
        new Client(ip, port);
    }
}
