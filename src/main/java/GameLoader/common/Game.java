package GameLoader.common;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import static GameLoader.common.Serializables.*;

import java.util.List;

public interface Game {
    // derived classes should provide default constructor

    void makeMove(Command move);
    boolean isMoveLegal(Command move);

    void start(String settings, int seed);
    String getSettings();

    enum state { UNFINISHED, DRAW, P0_WON, P1_WON }
    state getState();
    int getTurn();
    PlayViewModel createViewModel(Client user, int id);

    // "static" methods
    String getName();
    List<String> possibleSettings();
}
