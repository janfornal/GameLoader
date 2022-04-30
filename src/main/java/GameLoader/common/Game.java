package GameLoader.common;

import java.util.Set;

public interface Game {
    // derived classes should provide default constructor

    void makeMove(Command move);
    boolean isMoveLegal(Command move);

    void start(String settings, int seed);
    String getSettings();

    enum state { UNFINISHED, DRAW, P0_WON, P1_WON };
    state getState();

    // "static" methods
    String getName();
    Set<String> possibleSettings();
}
