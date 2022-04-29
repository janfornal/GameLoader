package GameLoader.common;

public class TicTacToe implements Game {
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
