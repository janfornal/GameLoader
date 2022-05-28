package GameLoader.server;

import GameLoader.common.Service;
import GameLoader.common.Game;
import static GameLoader.common.Utility.IntPair;

/**
 * This class is thread-safe
 */
public class SimpleEloManager implements EloManager {
    @Override
    public IntPair calculate(int eloP0, int eloP1, int gamesPlayedP0, int gamesPlayedP1, Game.state result) {
        if (result == Game.state.P0_WON)
            return new IntPair(eloP0+1, eloP1-1);
        if (result == Game.state.P1_WON)
            return new IntPair(eloP0-1, eloP1+1);
        if (result == Game.state.DRAW)
            return new IntPair(eloP0-1, eloP1-1);
        return new IntPair(eloP0, eloP1);
    }
}
