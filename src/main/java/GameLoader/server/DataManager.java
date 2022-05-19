package GameLoader.server;

import GameLoader.common.PlayerInfo;

/**
 * This class is not thread-safe
 */
public interface DataManager extends AutoCloseable {
    /**
     * @return true if player with given name exists, false otherwise
     */
    default boolean playerExists(String name) {
        return getPlayerId(name) != null;
    }

    /**
     * @return id if player with given name exists, null otherwise
     */
    Integer getPlayerId(String player);

    /**
     * @return name if player with given id exists, null otherwise
     */
    String getPlayerName(int player);

    /**
     * @return password if player with given id exists, null otherwise
     */
    Long getPlayerPassword(int player);

    /**
     * @return password if player with given name exists, null otherwise
     */
    default Long getPlayerPassword(String player) {
        Integer id = getPlayerId(player);
        return id == null ? null : getPlayerPassword(id);
    }

    /**
     * @return id of registered player if successful, null otherwise
     */
    Integer registerPlayer(String name, long password);

    /**
     * @return true if game with given name exists, false otherwise
     */
    default boolean gameExists(String name) {
        return getPlayerId(name) != null;
    }

    /**
     * @return id if game with given name exists, null otherwise
     */
    Integer getGameId(String name);

    /**
     * @return name if game with given id exists, null otherwise
     */
    String getGameName(int game);

    /**
     * @return id of registered game if successful, null otherwise
     */
    Integer registerGame(String name);

    /**
     * @return elo value if pair (player, game) exists in elo table, {@code Service.DEFAULT_ELO} otherwise
     */
    int getElo(int player, int game);

    /**
     *  if player or game does not exist nothing happens
     */
    void setElo(int player, int game, int elo);

    /**
     * @return elo value if pair (player, game) exists in elo table, {@code Service.DEFAULT_ELO} otherwise
     * @throws NullPointerException if player or game does not exist
     */
    default int getElo(String player, String game) {
        return getElo(getPlayerId(player), getGameId(game));
    }

    /**
     * @throws NullPointerException if player or game does not exist
     */
    default void setElo(String player, String game, int elo) {
        setElo(getPlayerId(player), getGameId(game), elo);
    }

    /**
     * @return requested PlayerInfo object
     * @throws NullPointerException if player or game does not exist
     */
    default PlayerInfo getPlayerInfo(String playerName, String gameName) {
        return new PlayerInfo(
                playerName,
                getElo(getPlayerId(playerName), getGameId(gameName))
        );
    }
}
