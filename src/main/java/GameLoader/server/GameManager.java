package GameLoader.server;

import GameLoader.common.*;
import javafx.util.Pair;

import static GameLoader.common.Utility.IntDoublePair;
import static GameLoader.common.Messages.*;
import static GameLoader.common.Serializables.*;

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

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Map<String, RoomInfo> roomsToJoin = new LinkedHashMap<>();

    private final Random rn = new Random();

    private record GameInstance(int gameId, Game game, String p0, String p1) {}

    public boolean isPlaying(String name) {
        return gameMap.get(name) != null;
    }

    public synchronized void processCreateRoomMessage(CreateRoomMessage msg, Connection c) {
        String name = c.getName();

        if (isPlaying(name)) {
            c.sendError("you are already playing a game");
            return;
        }
        if (roomsToJoin.containsKey(name)) {
            c.sendError("you already created a room");
            return;
        }

        String game = msg.game();
        String settings = msg.settings();

        if (!server.gameTypeManager.checkSettings(game, settings)) {
            c.sendError("game <" + game + "> does not support setting: <" + settings + ">");
            return;
        }

        RoomInfo info = new RoomInfo(game, settings, server.dataManager.getPlayerInfo(name, game));
        roomsToJoin.put(name, info);
    }

    public synchronized void processGetRoomListMessage(GetRoomListMessage ignored, Connection c) {
        c.sendMessage(new RoomListMessage(new ArrayList<>(roomsToJoin.values())));
    }

    public synchronized void processJoinRoomMessage(JoinRoomMessage msg, Connection c) {
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

        if (p1.equals(p0)) {
            c.sendError("you cannot play with yourself");
            return;
        }

        if (gameMap.containsKey(p0) || gameMap.containsKey(p1)) { // FIXME is this check necessary?
            String err = p0 + " is playing: " + gameMap.containsKey(p0) + " ; " +
                         p1 + " is playing: " + gameMap.containsKey(p1);

            server.userManager.sendErrorTo(err, p0, p1);
            return;
        }

        String gameName = info.game();
        String settings = info.settings();

        Game g = server.gameTypeManager.createGame(gameName, settings);
        if (g == null) {
            server.userManager.sendErrorTo("encountered error while constructing game <" +
                    gameName + "> with settings: <" + settings + ">", p0, p1);
            return;
        }

        int seed = rn.nextInt(2);
        g.start(settings, seed);

        int id = server.dataManager.nextId();
        GameInstance instance = new GameInstance(id, g, p0, p1);
        gameMap.put(p0, instance);
        gameMap.put(p1, instance);

        StartGameMessage stg = new StartGameMessage(
                gameName, info.settings(), seed,
                server.dataManager.getPlayerInfo(p0, gameName),
                server.dataManager.getPlayerInfo(p1, gameName));
        server.userManager.sendMessageTo(stg, p0, p1);
    }

    public /* unsynchronized */ void processMoveMessage(MoveMessage msg, Connection c) {
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

            server.userManager.sendMessageTo(msg, cmd.getPlayer() == 0 ? g.p1 : g.p0);

            if (game.getState() != Game.state.UNFINISHED)
                reportGameEnded(g);
        }
    }

    private synchronized void reportGameEnded(GameInstance g) {
        gameMap.remove(g.p0);
        gameMap.remove(g.p1);

        String game = g.game.getName();

        //TODO NOW!
         IntDoublePair res = server.eloManager.calculate(
                server.dataManager.getElo(g.p0, game),
                server.dataManager.getElo(g.p1, game),
                -1,
                -1,
                g.game.getState()
        );
        server.dataManager.setElo(g.p0, game, res.first(),res.third());
        server.dataManager.setElo(g.p1, game, res.second(),res.fourth());



        int ww = g.game.getState().ordinal();
        if(ww == 1) ww = 0;
        if(ww == 2) ww = 1;
        if(ww == 3) ww = -1;
        server.dataManager.insertGameInstance(game, g.p0, g.p1, ww);

    }

    public synchronized void reportConnectionClosed(Connection c) {
        String name = c.getName();
        roomsToJoin.remove(name);
        GameInstance g = gameMap.get(name);

        if (g == null)
            return;

        Command res = new ResignationCommand(g.p0.equals(name) ? 0 : 1);
        processMoveMessage(new MoveMessage(res), c);
    }

    public synchronized void processChatMessage(ChatMessage m, Connection c) {
        GameInstance g = gameMap.get(c.getName());
        server.userManager.sendMessageTo(m, g.p0);
        server.userManager.sendMessageTo(m, g.p1);
    }

    public void processQueryMessage(QueryMessage m, Connection c) {
        Query que = m.query();
        if (que == null) {
            c.sendError("query is null");
            return;
        }
        if(que instanceof StatisticsQuery) {
            ArrayList<Pair<String, Integer>> ret = server.dataManager.showGameStatistics(que.getGame());
            c.sendMessage(new AnswerMessage(new StatisticsAnswer(ret)));
        }
        if(que instanceof GamesQuery) {
            int won[] = new int[3];
            for(int i=0; i<3; i++) {
                won[i] = server.dataManager.getGameStates(que.getPlayer(), que.getGame(), i-1);
            }
            c.sendMessage(new AnswerMessage(new GamesAnswer(que.getGame(), won[2], won[1], won[0])));
        }
        if(que instanceof EloQuery) {
            int elo = server.dataManager.getElo(que.getPlayer(), que.getGame());
            c.sendMessage(new AnswerMessage(new EloAnswer(que.getGame(), elo)));
        }
    }
}
