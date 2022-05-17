package GameLoader.server;

import GameLoader.common.AbstractService;
import GameLoader.common.Game;
import GameLoader.utility.IntPair;

public class SimpleEloManager implements EloManager {
    public SimpleEloManager(AbstractService ignored) {}

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
