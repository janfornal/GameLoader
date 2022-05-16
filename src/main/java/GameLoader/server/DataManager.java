package GameLoader.server;

public interface DataManager extends AutoCloseable {
    int getPlayerId(String name);
    String getPlayerName(int i);
    long getPlayerPassword(int i);
    int registerPlayer(String name, long password);

    int getGameId(String name);
    String getGameName(int i);
    int registerGame(String name);

    int getElo(int player, int game);
    void setElo(int player, int game, int elo);
}
