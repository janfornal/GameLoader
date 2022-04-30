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

    PrintStream GENERAL_DBG_STREAM = System.out; // TEMPORARY DEBUGGING
    PrintStream INC_MESSAGE_DBG_STREAM = System.out; // TEMPORARY DEBUGGING
    PrintStream OUT_MESSAGE_DBG_STREAM = System.out; // TEMPORARY DEBUGGING

    void processMessage(Message.Any message, Connection connection);

    void reportGameEnded(Game game); // TODO: delete this?

    void reportConnectionClosed(Connection connection);
}
