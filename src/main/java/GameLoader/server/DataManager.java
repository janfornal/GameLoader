package GameLoader.server;

import javafx.util.Pair;

import java.util.ArrayList;

import static GameLoader.common.Serializables.PlayerInfo;

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
    String getPlayerPassword(int player);

    /**
     * @return password if player with given name exists, null otherwise
     */
    default String getPlayerPassword(String player) {
        Integer id = getPlayerId(player);
        return id == null ? null : getPlayerPassword(id);
    }

    /**
     * @return id of registered player if successful, null otherwise
     */
    Integer registerPlayer(String name, String password);

    /**
     * @return true if game with given name exists, false otherwise
     */
    default boolean gameExists(String name) {
        return getGameId(name) != null;
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

    /**
     * @return next unique id
     */
    int nextId();

    /**
     * @return show ranking of all players
     */
    ArrayList<Pair<String, Integer>> showGameStatistics(String gameName);
}
