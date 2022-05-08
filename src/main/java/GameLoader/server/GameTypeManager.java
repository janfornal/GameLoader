package GameLoader.server;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToe;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.common.AbstractService;
import GameLoader.common.Game;

import java.lang.reflect.Constructor;
import java.util.*;

public class GameTypeManager {
    public GameTypeManager(AbstractService ignored) {
        registerGameClass(SimpleTicTacToe.class);
        registerGameClass(DotsAndBoxes.class);
    }

    private record GameType(Set<String> settings, Constructor<? extends Game> constructor) {}
    private final Map<String, GameType> gameTypes = new HashMap<>();

    public synchronized Set<String> getGameNames() {
        return Collections.unmodifiableSet(gameTypes.keySet());
    }

    public synchronized Set<String> possibleSettings(String name) {
        GameType type = gameTypes.get(name);
        if (type == null)
            return null;

        return type.settings;
    }

    public synchronized boolean areSettingsCorrect(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null)
            return false;

        return type.settings.contains(settings);
    }

    public synchronized Game createGame(String name, String settings) {
        GameType type = gameTypes.get(name);
        if (type == null || !type.settings.contains(settings))
            return null;

        try {
            return type.constructor.newInstance();
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            System.err.println("encountered error while constructing: <" + name + "> with settings <" + settings + ">");
            e.printStackTrace();
            return null;
        }
    }

    public synchronized boolean registerGameClass(Class<? extends Game> cl) {
        try {
            Constructor<? extends Game> constructor = cl.getConstructor();
            Game g = constructor.newInstance();

            String name = g.getName();
            Set<String> settings = g.possibleSettings();

            Objects.requireNonNull(name);
            if (gameTypes.containsKey(name))
                throw new RuntimeException("this game is already registered");
            if (settings.isEmpty())
                throw new RuntimeException("settings set is empty");

            gameTypes.put(name, new GameType(settings, constructor));
            return true;
        }
        catch (RuntimeException | ReflectiveOperationException e) {
            System.err.println("encountered error while registering " + cl);
            e.printStackTrace();
            return false;
        }
    }

    public void processGetGameListMessage(Message.GetGameList ignored, Connection c) {
        c.sendMessage(new Message.GameList()); // TODO implement
    }

}

