package GameLoader.server;

import GameLoader.common.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is thread-safe
 */

public class GameManager {
    private final Server server;
    public GameManager(Server s) {
        server = s;
    }

    private final Map<Integer, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Map<Integer, RoomInfo> roomsToJoin = new LinkedHashMap<>();

    private final Random rn = new Random();

    private record GameInstance(int gameId, Game game, int p0, int p1) {}

    public boolean isPlaying(int id) {
        return gameMap.get(id) != null;
    }

    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {
        int id = c.getId();

        if (isPlaying(id)) {
            c.sendError("you are already playing a game");
            return;
        }
        if (roomsToJoin.containsKey(id)) {
            c.sendError("you already created a room");
            return;
        }

        String game = msg.game();
        String settings = msg.settings();

        if (!server.gameTypeManager.areSettingsCorrect(game, settings)) {
            c.sendError("game <" + game + "> does not support setting: <" + settings + ">");
            return;
        }

        RoomInfo info = new RoomInfo(game, settings, server.dataManager.getPlayerInfo(id, game));
        roomsToJoin.put(id, info);
    }

    public synchronized void processGetRoomListMessage(Message.GetRoomList ignored, Connection c) {
        c.sendMessage(new Message.RoomList(new ArrayList<>(roomsToJoin.values())));
    }

    public synchronized void processJoinRoomMessage(Message.JoinRoom msg, Connection c) {
        RoomInfo info = msg.room();

        int p0 = info.p0().id();
        int p1 = c.getId();

        RoomInfo ourInfo = roomsToJoin.get(p0);

        if (!info.equals(ourInfo)) {
            c.sendError("this room no longer exists");
            return;
        }

        roomsToJoin.remove(p0);
        roomsToJoin.remove(p1);

        if (p0 == p1) {
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

        GameInstance instance = new GameInstance(0, g, p0, p1); // TODO gameid (for game history)
        gameMap.put(p0, instance);
        gameMap.put(p1, instance);

        Message.StartGame stg = new Message.StartGame(
                gameName, info.settings(), seed,
                server.dataManager.getPlayerInfo(p0, gameName),
                server.dataManager.getPlayerInfo(p1, gameName));
        server.connectionManager.sendMessageTo(stg, p0, p1);
    }

    public /* unsynchronized */ void processMoveMessage(Message.Move msg, Connection c) {
        Command cmd = msg.move();

        if (cmd == null) {
            c.sendError("cmd is null");
            return;
        }

        GameInstance g = gameMap.get(c.getId());

        if (g == null) {
            c.sendError("you are not playing any game");
            return;
        }

        if (!(c.getId() == (cmd.getPlayer() == 0 ? g.p0 : g.p1))) {
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
        gameMap.remove(g.p0);
        gameMap.remove(g.p1);
    }

    public synchronized void processEndConnectionMessage(Message.EndConnection ignored, Connection c) {
        roomsToJoin.remove(c.getId());
        if (gameMap.containsKey(c.getId())) {
            GameInstance g = gameMap.get(c.getId());
            if (g.p0 == c.getId())
                server.connectionManager.sendMessageTo(new Message.Resign(), g.p1);
            else
                server.connectionManager.sendMessageTo(new Message.Resign(), g.p0);
            reportGameEnded(g);
        }
    }

    public synchronized void processLeaveRoomMessage(Message.LeaveRoom ignored, Connection c) {
        GameInstance g = gameMap.get(c.getId());
        if (g.game.getState() == Game.state.UNFINISHED)
            System.err.println("Something went wrong while ending the game: " + g);
        reportGameEnded(g);
    }

    public synchronized void processResignMessage(Message.Resign m, Connection c) {
        GameInstance g = gameMap.get(c.getId());
        if (g.game.getState() != Game.state.UNFINISHED)
            System.err.println("You cannot resign from ended game: " + g);
        if (g.p0 == c.getId())
            server.connectionManager.sendMessageTo(m, g.p1);
        else
            server.connectionManager.sendMessageTo(m, g.p0);
        reportGameEnded(g);
    }

    public synchronized void processChatMessage(Message.ChatMessage m, Connection c) {
        GameInstance g = gameMap.get(c.getName());
        server.connectionManager.sendMessageTo(m, g.p0);
        server.connectionManager.sendMessageTo(m, g.p1);
    }
}
