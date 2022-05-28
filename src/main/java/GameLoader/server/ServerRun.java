package GameLoader.server;

import GameLoader.common.Service;

public class ServerRun {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : Service.defaultPort;
        new Server(port);
    }
}
