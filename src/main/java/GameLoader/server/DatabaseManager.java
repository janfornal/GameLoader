package GameLoader.server;

public interface DatabaseManager {
    String getPlayerName(int i);
    int getPlayerId(String name);

    String getGameName(int i);
    int getGameId(String name);
}
