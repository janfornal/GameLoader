package GameLoader.common;

import java.io.Serializable;

public interface Game {
    interface Command extends Serializable {
        String getPlayer();
    }

    Game createNewGame();
    Game createNewGame(Game.GameInfo info);
    void makeMove(Command cmd);
    boolean isLegal(Command cmd);
    boolean isFinished();
    PlayerInfo[] players();
    GameInfo getGameInfo();

    // FIXME: gameInfo should be a record ?
    interface GameInfo extends Serializable { // remember: implement equals & hashcode !!
        String getInfo();
        String getName();
        PlayerInfo getPlayer();
    }
}
