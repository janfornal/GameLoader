package GameLoader.server;

import GameLoader.common.PlayerInfo;

public interface DataManager extends AutoCloseable {
    Integer getPlayerId(String name);
    String getPlayerName(int i);
    Integer getPlayerPassword(int i); // FIXME how to store passwords properly?
    Integer registerPlayer(String name, int password);

    Integer getGameId(String name);
    String getGameName(int i);
    Integer registerGame(String name);

    int getElo(int player, int game);
    void setElo(int player, int game, int elo);

    default int getElo(int player, String game) {
        return getElo(player, getGameId(game));
    }

    default void setElo(int player, String game, int elo) {
        setElo(player, getGameId(game), elo);
    }

    default PlayerInfo getPlayerInfo(String playerName, String gameName) {
        return new PlayerInfo(
                playerName,
                getElo(getPlayerId(playerName), getGameId(gameName))
        );
    }
}
