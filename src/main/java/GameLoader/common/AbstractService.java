package GameLoader.common;

import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface AbstractService {
    ExecutorService execNormal = Executors.newCachedThreadPool();
    ExecutorService execDaemon = Executors.newCachedThreadPool(run -> {
        Thread th = new Thread(run);
        th.setDaemon(true);
        return th;
    });

    PrintStream INC_MESSAGE_DBG_STREAM = System.out; // report messages that you received
    PrintStream SND_MESSAGE_DBG_STREAM = System.out; // report messages that you tried to send
    PrintStream SNT_MESSAGE_DBG_STREAM = null; // report messages that you actually sent

    void processMessage(Message.Any message, Connection connection);

    void reportConnectionClosed(Connection connection);
}
