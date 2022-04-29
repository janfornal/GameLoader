package GameLoader.common;

import GameLoader.games.Command;
import GameLoader.games.Game;

public class TicTacToe implements Game {
    private PlayerInfo p1, p2;
    private SYM p1Sym;

    private enum SYM { X, O, EMPTY } // X begins

    public TicTacToe(TicTacToeGameInfo info) {

    }




    @Override
    public Game createNewGame() {
        return null;
    }

    @Override
    public Game createNewGame(GameInfo info) {
        return null;
    }

    @Override
    public void makeMove(Command cmd) {

    }

    @Override
    public boolean isLegal(Command cmd) {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public PlayerInfo[] players() {
        return new PlayerInfo[0];
    }

    @Override
    public GameInfo getGameInfo() {
        return null;
    }
}
