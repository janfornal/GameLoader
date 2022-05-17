package GameLoader.common;

import java.io.OutputStream;
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

    PrintStream NULL_STREAM = new PrintStream(OutputStream.nullOutputStream());
    PrintStream INFO_STREAM = System.out;
    PrintStream ERROR_STREAM = System.err;

    PrintStream INC_MESSAGE = System.out; // report messages that you received
    PrintStream SND_MESSAGE = System.out; // report messages that you tried to send
    PrintStream SNT_MESSAGE = NULL_STREAM; // report messages that you actually sent

    void processMessage(Message.Any message, Connection connection);

    void reportConnectionClosed(Connection connection);
}
