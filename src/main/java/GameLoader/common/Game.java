package GameLoader.common;

import java.io.Serializable;

public interface Game {
    interface Command extends Serializable {


    }

    public Game createNewGame();
    void makeMove(Command cmd);
    boolean isLegal(Command cmd);
    PlayerInfo[] players();
    GameInfo getGameInfo();

    interface GameInfo extends Serializable {
        String getInfo();
        String getName();
        PlayerInfo getPlayer();
    }
}
