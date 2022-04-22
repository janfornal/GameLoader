package GameLoader.client;

import java.net.Socket;
import java.util.function.Consumer;

public class Connection {
    Consumer<Message> accMess;
    Socket s;
    String name;

}
