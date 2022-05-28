package GameLoader.client;

import GameLoader.common.Service;

import java.io.IOException;

public class ClientRun {
    public static void main(String[] args) throws IOException {
        String ip = args.length > 0 ? args[0] : Service.defaultIP;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : Service.defaultPort;
        new Client(ip, port);
    }
}
