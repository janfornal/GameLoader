package GameLoader.server;

import GameLoader.common.Connection;

public class ServerRun {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : Connection.defaultPort;
        new Server(port);
    }
}
