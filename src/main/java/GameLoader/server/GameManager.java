package GameLoader.server;

import GameLoader.common.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Server server;
    public GameManager(Server s) {
        server = s;
    }

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Map<String, RoomInfo> roomsToJoin = new LinkedHashMap<>();

    private final Random rn = new Random();

    private record GameInstance(Game game, String p0, String p1) {}

    public boolean isPlaying(String p) {
        return gameMap.get(p) != null;
    }

    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {
        String pn = c.getName();

        if (isPlaying(pn)) {
            c.sendError("you are already playing a game");
            return;
        }
        if (roomsToJoin.containsKey(pn)) {
            c.sendError("you already created a room");
            return;
        }

        String game = msg.game();
        String settings = msg.settings();

        if (!server.gameTypeManager.areSettingsCorrect(game, settings)) {
            c.sendError("game <" + game + "> does not support setting: <" + settings + ">");
            return;
        }

        RoomInfo info = new RoomInfo(game, settings, new PlayerInfo(c.getName()));
        roomsToJoin.put(pn, info);
    }

    public synchronized void processGetRoomListMessage(Message.GetRoomList ignored, Connection c) {
        c.sendMessage(new Message.RoomList(new ArrayList<>(roomsToJoin.values())));
    }

    public synchronized void processJoinRoomMessage(Message.JoinRoom msg, Connection c) {
        RoomInfo info = msg.room();

        String p0 = info.p0().name();
        String p1 = c.getName();

        RoomInfo ourInfo = roomsToJoin.get(p0);

        if (!info.equals(ourInfo)) {
            c.sendError("this room no longer exists");
            return;
        }

        roomsToJoin.remove(p0);
        roomsToJoin.remove(p1);

        if (p0.equals(p1)) {
            c.sendError("you cannot play with yourself");
            return;
        }

        if (gameMap.containsKey(p0) || gameMap.containsKey(p1)) { // FIXME is this check necessary?
            String err = p0 + " is playing: " + gameMap.containsKey(p0) + " ; " +
                         p1 + " is playing: " + gameMap.containsKey(p1);

            server.connectionManager.sendErrorTo(err, p0, p1);
            return;
        }

        String gameName = info.game();
        String settings = info.settings();

        Game g = server.gameTypeManager.createGame(gameName, settings);
        if (g == null) {
            server.connectionManager.sendErrorTo("encountered error while constructing game <" +
                    gameName + "> with settings: <" + settings + ">", p0, p1);
            return;
        }

        int seed = rn.nextInt(2);
        g.start(settings, seed);

        GameInstance instance = new GameInstance(g, p0, p1);
        gameMap.put(p0, instance);
        gameMap.put(p1, instance);

        Message.StartGame stg = new Message.StartGame(gameName, info.settings(), seed,
                new PlayerInfo(p0), new PlayerInfo(p1));
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

        if (!c.getName().equals(cmd.getPlayer() == 0 ? g.p0 : g.p1)) {
            c.sendError("connection name does not match move player name");
            return;
        }

        synchronized (g) {
            Game game = g.game;
            if (!game.isMoveLegal(cmd)) {
                c.sendError("this move is not legal");
                return;
            }
            game.makeMove(cmd);

            server.connectionManager.sendMessageTo(msg, g.p0, g.p1);

            if (game.getState() != Game.state.UNFINISHED)
                reportGameEnded(g);
        }
    }

    private synchronized void reportGameEnded(GameInstance g) {
        if (g.game.getState() == Game.state.UNFINISHED)
            System.err.println("Something went wrong while ending the game: " + g);

        gameMap.remove(g.p0);
        gameMap.remove(g.p1);
    }
}
