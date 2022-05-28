package GameLoader.common;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static GameLoader.common.Messages.*;

public interface Service {
    ExecutorService execNormal = Executors.newCachedThreadPool();
    ExecutorService execDaemon = Executors.newCachedThreadPool(run -> {
        Thread th = new Thread(run);
        th.setDaemon(true);
        return th;
    });

    PrintStream NULL_STREAM = new PrintStream(OutputStream.nullOutputStream());
    PrintStream ERROR_STREAM = System.err;
    PrintStream INFO_STREAM = System.out;

    PrintStream INC_MESSAGE = INFO_STREAM; // report messages that you received
    PrintStream SND_MESSAGE = INFO_STREAM; // report messages that you tried to send
    PrintStream SNT_MESSAGE = NULL_STREAM; // report messages that you actually sent

    PrintStream DB_DRIVER_ERROR_STREAM = ERROR_STREAM;
    PrintStream DB_CONNECTION_ERROR_STREAM = NULL_STREAM;
    PrintStream DB_CONNECTION_INFO_STREAM = NULL_STREAM;

    PrintStream DB_QUERY_CALL_STREAM = NULL_STREAM;
    PrintStream DB_QUERY_RESULT_STREAM = INFO_STREAM;

    PrintStream GAME_TYPE_ERROR_STREAM = ERROR_STREAM;
    PrintStream GAME_TYPE_INFO_STREAM = INFO_STREAM;

    // constants related to database connections are stored in server.DatabaseConnectionFactory

    int DEFAULT_ELO = 1000;

    PasswordManager passwordManager = new PasswordManager();
    GameTypeManager gameTypeManager = new DynamicGameTypeManager("games");

    void processMessage(AnyMessage message, Connection connection);

    void reportConnectionClosed(Connection connection);
}
