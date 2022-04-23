package GameLoader.common;

import GameLoader.common.messages.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract public class AbstractService {
    public static final ExecutorService execDaemon = Executors.newCachedThreadPool(run -> {
        Thread th = new Thread(run);
        th.setDaemon(true);
        return th;
    });
    private static AbstractService instance = null;

    protected AbstractService() {
        if (instance == null)
            instance = this;
        else
            throw new RuntimeException("Singleton pattern broken");
    }

    public static AbstractService getInstance() {
        if (instance == null)
            throw new RuntimeException("Create AbstractService first!");
        else
            return instance;
    }

    abstract public void processMessage(Message m);

    abstract public void reportGameEnded(Game.GameInstance gm);
}
