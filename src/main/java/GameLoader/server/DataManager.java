package GameLoader.server;

import GameLoader.common.PlayerInfo;

public interface DataManager extends AutoCloseable {
    int getPlayerId(String name);
    String getPlayerName(int i);
    int getPlayerPassword(int i); // FIXME how to store passwords properly?
    int registerPlayer(String name, int password);

    int getGameId(String name);
    String getGameName(int i);
    int registerGame(String name);

    int getElo(int player, int game);
    void setElo(int player, int game, int elo);

    default int getElo(int player, String game) {
        return getElo(player, getGameId(game));
    }

    default void setElo(int player, String game, int elo) {
        setElo(player, getGameId(game), elo);
    }

    default PlayerInfo getPlayerInfo(int playerId, int gameId) {
        return new PlayerInfo(
                getPlayerName(playerId),
                playerId,
                getElo(playerId, gameId)
        );
    }

    default PlayerInfo getPlayerInfo(int playerId, String game) {
        return getPlayerInfo(playerId, getGameId(game));
    }
}
