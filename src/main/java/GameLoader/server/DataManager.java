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
    double getRD(int player,int game);
    /**
     *  if player or game does not exist nothing happens
     */
    void setElo(int player, int game, int elo, double rd);
    /**
     * @return elo value if pair (player, game) exists in elo table, {@code Service.DEFAULT_ELO} otherwise
     * @throws NullPointerException if player or game does not exist
     */
    default int getElo(String player, String game) {
        return getElo(getPlayerId(player), getGameId(game));
    }
    default double getRD(String player, String game)  {return getRD(getPlayerId(player), getGameId(game));}
    /**
     * @throws NullPointerException if player or game does not exist
     */
    default void setElo(String player, String game, int elo, double rd) {
        setElo(getPlayerId(player), getGameId(game), elo, rd);
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
     * @return insert instance of game to history of games
     */
    void insertGameInstance(int game, int p0, int p1, int win);

    default void insertGameInstance(String game, String p0, String p1, int win) {
        insertGameInstance(getGameId(game), getPlayerId(p0), getPlayerId(p1), win);
    }

    /**
     * @return show ranking of all players
     */
    ArrayList<Pair<String, Integer>> showGameStatistics(String gameName);

    /**
     * @return returns number of won/lost/tied matches
     */
    int getGameStates(int which, int gameId, int player, int winner);

    default int getGameStates(String player, String game, int win) {
        int gameId = getGameId(game);
        int playerId = getPlayerId(player);
        int s0 = getGameStates(0, gameId, playerId, win);
        int s1 = getGameStates(1, gameId, playerId, -win);
        return s0 + s1;
    }
}
