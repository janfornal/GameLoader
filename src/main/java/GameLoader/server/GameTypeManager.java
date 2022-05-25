package GameLoader.server;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToe;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.common.Service;
import GameLoader.common.Game;
import GameLoader.games.TicTacToe.TicTacToe;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This class is not thread-safe
 */
public class GameTypeManager {
    private final Service service;
    public GameTypeManager(Service s) {
        service = s;
        registerGameClass(SimpleTicTacToe.class);
        registerGameClass(DotsAndBoxes.class);
        registerGameClass(TicTacToe.class);
    }

    private record GameType(List<String> settings, Constructor<? extends Game> constructor) {}
    private final Map<String, GameType> gameTypes = new HashMap<>();

    public List<String> getGameNames() {
        return new ArrayList<>(gameTypes.keySet());
    }

    public List<String> possibleSettings(String name) {
        GameType type = gameTypes.get(name);
        if (type == null)
            return null;

        return type.settings;
    }

    public boolean areSettingsCorrect(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null)
            return false;

        return type.settings.contains(settings);
    }

    public Game createGame(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null || !type.settings.contains(settings))
            return null;

        try {
            return type.constructor.newInstance();
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            service.ERROR_STREAM.println("encountered error while constructing: <" + name + "> with settings <" + settings + ">");
            e.printStackTrace(service.ERROR_STREAM);
            return null;
        }
    }

    public boolean registerGameClass(Class<? extends Game> cl) {
        try {
            Constructor<? extends Game> constructor = cl.getConstructor();
            Game g = constructor.newInstance();

            String name = g.getName();
            List<String> settings = g.possibleSettings();

            Objects.requireNonNull(name);
            if (gameTypes.containsKey(name))
                throw new RuntimeException("this game is already registered");
            if (settings.isEmpty())
                throw new RuntimeException("settings set is empty");

            gameTypes.put(name, new GameType(settings, constructor));
            return true;
        }
        catch (RuntimeException | ReflectiveOperationException e) {
            service.ERROR_STREAM.println("encountered error while registering " + cl);
            e.printStackTrace(service.ERROR_STREAM);
            return false;
        }
    }

    public void processGetGameListMessage(Message.GetGameList ignored, Connection c) {
        c.sendMessage(new Message.GameList()); // TODO implement
    }

}

