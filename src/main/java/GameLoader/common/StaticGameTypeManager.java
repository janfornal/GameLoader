package GameLoader.common;

import GameLoader.games.PaperSoccer.PaperSoccer;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.games.TicTacToe.TicTacToe;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */
public class StaticGameTypeManager implements GameTypeManager {
    public StaticGameTypeManager() {
        registerGameClass(DotsAndBoxes.class);
        registerGameClass(TicTacToe.class);
        registerGameClass(PaperSoccer.class);
    }

    private record GameType(List<String> settings, Supplier<Game> factory) {}
    private final Map<String, GameType> gameTypes = new HashMap<>();

    @Override
    public List<String> getGameNames() {
        return List.copyOf(gameTypes.keySet());
    }

    @Override
    public List<String> possibleSettings(String game) {
        GameType type = gameTypes.get(game);
        if (type == null)
            return Collections.emptyList();

        return type.settings;
    }

    @Override
    public boolean checkSettings(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null)
            return false;

        return type.settings.contains(settings);
    }

    @Override
    public Game createGame(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null || !type.settings.contains(settings))
            return null;

        return type.factory.get();
    }

    protected boolean registerGameClass(Class<? extends Game> cl) {
        Supplier<Game> factory = () -> {
            try {
                return cl.getConstructor().newInstance();
            } catch (RuntimeException | ReflectiveOperationException e) {
                e.printStackTrace(Service.GAME_TYPE_ERROR_STREAM);
                return null;
            }
        };

        Game g = factory.get();

        if (g == null) {
            Service.GAME_TYPE_ERROR_STREAM.println("creation of " + cl + " failed");
            return false;
        }

        String name = g.getName();
        List<String> settings = g.possibleSettings();

        if (name == null || gameTypes.containsKey(name)) {
            Service.GAME_TYPE_ERROR_STREAM.println("name: \"" + name + "\" of " + cl + " is invalid");
            return false;
        }

        if (settings == null || settings.isEmpty()) {
            Service.GAME_TYPE_ERROR_STREAM.println("settings: \"" + settings + "\" of " + cl + " are invalid");
            return false;
        }

        gameTypes.put(name, new GameType(settings, factory));
        Service.GAME_TYPE_INFO_STREAM.println(cl + " successfully registered game: " + name);
        return true;
    }
}

