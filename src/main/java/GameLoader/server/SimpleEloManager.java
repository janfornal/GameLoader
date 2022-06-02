package GameLoader.server;

import GameLoader.common.Service;
import GameLoader.common.Game;
import GameLoader.common.Utility;

import static GameLoader.common.Utility.IntDoublePair;

/**
 * This class is thread-safe
 */

public class SimpleEloManager implements EloManager {
    @Override
    public IntDoublePair calculate(int eloP0, int eloP1, double rd1, double rd2, Game.state result) {
        if (result == Game.state.P0_WON)
            return new IntDoublePair(eloP0+1, eloP1-1,0,0);
        if (result == Game.state.P1_WON)
            return new IntDoublePair(eloP0-1, eloP1+1,0,0);
        if (result == Game.state.DRAW)
            return new IntDoublePair(eloP0-1, eloP1-1,0,0);
        return new IntDoublePair(eloP0, eloP1,0,0);
    }

}
