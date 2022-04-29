package GameLoader.server;

import GameLoader.client.DotsAndBoxes;
import GameLoader.common.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Server server;
    public GameManager(Server s) {
        server = s;
        gameZoo.put("DotsAndBoxes", DotsAndBoxes.class);
        gameZoo.put("TicTacToe", TicTacToe.class);
    }

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Set<Game.GameInfo> toJoin = new LinkedHashSet<>();
    private final Map<String, Class<? extends Game>> gameZoo = new HashMap<>();

    private record GameInstance(Game game, String p1, String p2) {}

    public boolean isPlaying(String p) {
        return gameMap.get(p) != null;
    }

    // TODO: currently player can create two rooms simultaneously; this should not be allowed
    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {
        Game.GameInfo info = msg.game();

        if (info == null) {
            c.sendError("info is null");
            return;
        }
        if (isPlaying(c.getName())) {
            c.sendError("you are already playing a game");
            return;
        }

        String p = c.getName();

        if (!p.equals(info.getPlayer().name())) {
            c.sendError("connection name does not match create room player name");
            return;
        }

        if (!gameZoo.containsKey(info.getName())) {
            c.sendError("game <" + info.getName() + "> does not exist");
            return;
        }

        toJoin.add(info);
    }

    public synchronized void processGetRoomListMessage(Message.GetRoomList ignored, Connection c) {
        c.sendMessage(new Message.RoomList(new ArrayList<>(toJoin)));
    }

    public synchronized void processJoinRoomMessage(Message.JoinRoom msg, Connection c) {
        Game.GameInfo info = msg.game();
        if (!toJoin.contains(info)) {
            c.sendError("this room no longer exists");
            return;
        }
        toJoin.remove(info);

        String p1 = info.getPlayer().name();
        String p2 = c.getName();

        if (gameMap.containsKey(p1) || gameMap.containsKey(p2)) {
            String err = p1 + " is playing: " + gameMap.containsKey(p1) + " ; " +
                         p2 + " is playing: " + gameMap.containsKey(p2);

            server.connectionManager.sendMessageTo(new Message.Error(err), p1, p2);
            return;
        }

        String gameName = info.getName();
        Game g;
        try {
            g = gameZoo.get(gameName).getConstructor(info.getClass()).newInstance(info);
        } catch (Exception e) {
            c.sendError("<" + gameName + "> threw exception: " + e);
            return;
        }

        GameInstance instance = new GameInstance(g, p1, p2);
        gameMap.put(p1, instance);
        gameMap.put(p2, instance);

        Message.StartGame stg = new Message.StartGame(info.getPlayer(), new PlayerInfo(p2));
        server.connectionManager.sendMessageTo(stg, p1, p2);
    }

    public /* unsynchronized */ void processMoveMessage(Message.Move msg, Connection c) {
        Game.Command cmd = msg.move();

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
            if (!game.isLegal(cmd)) {
                c.sendError("this move is not legal");
                return;
            }
            game.makeMove(cmd);

            server.connectionManager.sendMessageTo(msg, g.p1(), g.p2());

            if (game.isFinished())
                reportGameEnded(g);
        }
    }

    private synchronized void reportGameEnded(GameInstance g) {
        gameMap.remove(g.p1());
        gameMap.remove(g.p2());
    }
}
