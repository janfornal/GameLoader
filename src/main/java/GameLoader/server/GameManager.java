package GameLoader.server;

import GameLoader.common.*;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class GameManager {
    private final Server server;
    public GameManager(Server s) {
        server = s;
        assert (registerGameType(SimpleTicTacToe.class));
    }

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Set<RoomInfo> toJoin = new LinkedHashSet<>();

    private record GameInstance(Game game, String p0, String p1) {}

    public boolean isPlaying(String p) {
        return gameMap.get(p) != null;
    }

    // TODO: currently player can create two rooms simultaneously; this should not be allowed
    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {
        if (isPlaying(c.getName())) {
            c.sendError("you are already playing a game");
            return;
        }

        GameType type = gameTypes.get(msg.game());

        if (type == null) {
            c.sendError("game <" + msg.game() + "> does not exist");
            return;
        }
        if (!type.settings().contains(msg.settings())) {
            c.sendError("game <" + msg.game() + "> does not support setting: " + msg.settings());
            return;
        }

        RoomInfo info = new RoomInfo(msg.game(), msg.settings(), new PlayerInfo(c.getName()));
        toJoin.add(info);
    }

    public synchronized void processGetRoomListMessage(Message.GetRoomList ignored, Connection c) {
        c.sendMessage(new Message.RoomList(new ArrayList<>(toJoin)));
    }

    public synchronized void processJoinRoomMessage(Message.JoinRoom msg, Connection c) {
        RoomInfo info = msg.room();
        if (!toJoin.contains(info)) {
            c.sendError("this room no longer exists");
            return;
        }
        toJoin.remove(info);

        String p0 = info.p0().name();
        String p1 = c.getName();

        if (gameMap.containsKey(p0) || gameMap.containsKey(p1)) {
            String err = p0 + " is playing: " + gameMap.containsKey(p0) + " ; " +
                         p1 + " is playing: " + gameMap.containsKey(p1);

            server.connectionManager.sendMessageTo(new Message.Error(err), p0, p1);
            return;
        }

        String gameName = info.game();
        Game g;
        try {
            g = gameTypes.get(gameName).sup().get();
        } catch (Exception e) {
            c.sendError("<" + gameName + "> threw exception: " + e);
            System.err.println("encountered error while constructing " + gameName);
            e.printStackTrace();
            return;
        }

        GameInstance instance = new GameInstance(g, p0, p1);
        gameMap.put(p0, instance);
        gameMap.put(p1, instance);

        Message.StartGame stg = new Message.StartGame(gameName, info.settings(), new PlayerInfo(p0), new PlayerInfo(p1), 0);
        server.connectionManager.sendMessageTo(stg, p0, p1);
    }

    public /* unsynchronized */ void processMoveMessage(Message.Move msg, Connection c) {
        Command cmd = msg.move();

        if (cmd == null) {
            c.sendError("cmd is null");
            return;
        }

        GameInstance g = gameMap.get(c.getName());

        if (g == null) {
            c.sendError("you are not playing any game");
            return;
        }

        if (!c.getName().equals(cmd.getPlayer() == 0 ? g.p0() : g.p1())) {
            c.sendError("connection name does not match move player name");
            return;
        }

        synchronized (g) {
            Game game = g.game();
            if (!game.isMoveLegal(cmd)) {
                c.sendError("this move is not legal");
                return;
            }
            game.makeMove(cmd);

            server.connectionManager.sendMessageTo(msg, g.p0(), g.p1());

            if (game.getState() != Game.state.UNFINISHED)
                reportGameEnded(g);
        }
    }

    private synchronized void reportGameEnded(GameInstance g) {
        gameMap.remove(g.p0());
        gameMap.remove(g.p1());
    }

    private record GameType(Set<String> settings, Supplier<Game> sup) {}
    private final Map<String, GameType> gameTypes = new HashMap<>();

    public synchronized boolean registerGameType(Class<? extends Game> cl) {
        try {
            Supplier<Game> sup = () -> {
                try {
                    return cl.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            Game g = sup.get();
            String name = g.getName();
            Set<String> settings = g.possibleSettings();

            Objects.requireNonNull(name);
            if (settings.isEmpty())
                throw new RuntimeException("settings set is empty");
            if (gameTypes.containsKey(name))
                throw new RuntimeException("this game is already registered");

            gameTypes.put(name, new GameType(settings, sup));
            return true;
        }
        catch (Exception e) {
            System.err.println("encountered error while registering " + cl);
            e.printStackTrace();
            return false;
        }
    }
}
