package GameLoader.server;

abstract public class AbstractDatabaseManager implements DatabaseManager {
    abstract protected void openConnection();

    @Override
    public String getPlayerName(int i) {
        return null;
    }

    @Override
    public int getPlayerId(String name) {
        return 0;
    }

    @Override
    public String getGameName(int i) {
        return null;
    }

    @Override
    public int getGameId(String name) {
        return 0;
    }
}
