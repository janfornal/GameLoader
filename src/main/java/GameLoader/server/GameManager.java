package GameLoader.server;

import GameLoader.common.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Server server;
    public GameManager(Server s) {
        server = s;
        // gameZoo.put("DotsAndBoxes", DotsAndBoxes.class);
        // gameZoo.put("TicTacToe", TicTacToe.class);
    }

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Set<RoomInfo> toJoin = new LinkedHashSet<>();
    private final Map<String, Class<? extends Game>> gameZoo = new HashMap<>();

    private record GameInstance(Game game, String p1, String p2) {}

    public boolean isPlaying(String p) {
        return gameMap.get(p) != null;
    }

    // TODO: currently player can create two rooms simultaneously; this should not be allowed
    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {
        if (isPlaying(c.getName())) {
            c.sendError("you are already playing a game");
            return;
        }

        String p = c.getName();

        if (!gameZoo.containsKey(msg.game())) {
            c.sendError("game <" + msg.game() + "> does not exist");
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
            String err = p0+ " is playing: " + gameMap.containsKey(p0) + " ; " +
                         p1 + " is playing: " + gameMap.containsKey(p1);

            server.connectionManager.sendMessageTo(new Message.Error(err), p0, p1);
            return;
        }

        String gameName = info.game();
        Game g;
        try {
            g = gameZoo.get(gameName).getConstructor(info.getClass()).newInstance(info);
        } catch (Exception e) {
            c.sendError("<" + gameName + "> threw exception: " + e);
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
        if (!c.getName().equals(cmd.getPlayer())) {
            c.sendError("connection name does not match move player name");
            return;
        }

        GameInstance g = gameMap.get(c.getName());

        if (g == null) {
            c.sendError("you are not playing any game");
            return;
        }

        synchronized (g) {
            Game game = g.game();
            if (!game.isMoveLegal(cmd)) {
                c.sendError("this move is not legal");
                return;
            }
            game.makeMove(cmd);

            server.connectionManager.sendMessageTo(msg, g.p1(), g.p2());

            if (game.getState() != Game.state.UNFINISHED)
                reportGameEnded(g);
        }
    }

    private synchronized void reportGameEnded(GameInstance g) {
        gameMap.remove(g.p1());
        gameMap.remove(g.p2());
    }
}
