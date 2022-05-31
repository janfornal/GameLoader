package GameLoader.common;

import java.util.List;
/**
 * This class is thread-safe
 */
public interface GameTypeManager {
    /**
     * @return list of names of all registered games
     */
    List<String> getGameNames();

    /**
     * @return list of settings for a given game if such game is registered, otherwise returns empty list
     */
    List<String> possibleSettings(String game);

    /**
     * @return check whether settings are correct for a given game
     */
    boolean checkSettings(String game, String settings);

    /**
     * @return game instance if manager succeeds, {@code null} otherwise
     */
    Game createGame(String name, String settings);

    /**
     * @return custom class loader that should be used when deserializing messages
     */
    ClassLoader getClassLoader();
}
