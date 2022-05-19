package GameLoader.server;

/**
 * This class is not thread-safe
 */
public class CachedDataManager implements DataManager {
    @Override
    public Integer getPlayerId(String name) {
        return null;
    }

    @Override
    public String getPlayerName(int i) {
        return null;
    }

    @Override
    public Long getPlayerPassword(int i) {
        return null;
    }

    @Override
    public Integer registerPlayer(String name, long password) {
        return null;
    }

    @Override
    public Integer getGameId(String name) {
        return null;
    }

    @Override
    public String getGameName(int i) {
        return null;
    }

    @Override
    public Integer registerGame(String name) {
        return null;
    }

    @Override
    public int getElo(int player, int game) {
        return 0;
    }

    @Override
    public void setElo(int player, int game, int elo) {

    }

    @Override
    public void close() {

    }
}
