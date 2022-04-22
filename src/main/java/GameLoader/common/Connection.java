package GameLoader.common;

import GameLoader.common.messages.Message;

import java.net.Socket;
import java.util.function.Consumer;

public class Connection {
    Consumer<Message> accMess;
    Socket s;
    String name;
    volatile boolean authorized;
        class ListeningConnection implements Runnable{

            @Override
            public void run() {

            }
        }

}
