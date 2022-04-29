package GameLoader.common;

import GameLoader.games.Game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface AbstractService {
    ExecutorService execNormal = Executors.newCachedThreadPool();
    ExecutorService execDaemon = Executors.newCachedThreadPool(run -> {
        Thread th = new Thread(run);
        th.setDaemon(true);
        return th;
    });

    void processMessage(Message.Any message, Connection connection);

    void reportGameEnded(Game game);

    void reportConnectionClosed(Connection connection);
}
