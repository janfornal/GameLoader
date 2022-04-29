package GameLoader.games;

import java.util.List;

public interface Game {
    // derived classes should provide default constructor

    void makeMove(Command move);
    boolean isMoveLegal(Command move);

    void start(GameSettings settings);
    GameSettings getSettings();

    enum state { UNFINISHED, DRAW, p0_WON, p1_WON };
    state getState();

    // "static" method
    List<GameSettings> possibleSettings();
}
