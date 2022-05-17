package GameLoader.server;

public class CachedDataManager implements DataManager {
    @Override
    public int getPlayerId(String name) {
        return 0;
    }

    @Override
    public String getPlayerName(int i) {
        return null;
    }

    @Override
    public long getPlayerPassword(int i) {
        return 0;
    }

    @Override
    public int registerPlayer(String name, long password) {
        return 0;
    }

    @Override
    public int getGameId(String name) {
        return 0;
    }

    @Override
    public String getGameName(int i) {
        return null;
    }

    @Override
    public int registerGame(String name) {
        return 0;
    }

    @Override
    public int getElo(int player, int game) {
        return 0;
    }

    @Override
    public void setElo(int player, int game, int elo) {

    }

    @Override
    public void close() throws Exception {

    }
}
