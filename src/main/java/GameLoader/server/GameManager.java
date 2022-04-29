package GameLoader.server;

import GameLoader.client.DotsAndBoxes;
import GameLoader.common.Connection;
import GameLoader.common.Game;
import GameLoader.common.Message;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private Server server;
    public GameManager(Server s) {
        server = s;
    }

    private final Map<String, GameInstance> gameMap = new ConcurrentHashMap<>();
    private final Set<Game.GameInfo> toJoin = new HashSet<>();

    private record GameInstance(Game game, String p1, String p2) {}

    public boolean isPlaying(String p) {
        return gameMap.get(p) != null;
    }

    public synchronized void processCreateRoomMessage(Message.CreateRoom msg, Connection c) {

    }

    public synchronized void processGetRoomListMessage(Message.GetRoomList msg, Connection c) {

    }

    public synchronized void processJoinRoomMessage(Message.JoinRoom msg, Connection c) {

    }

    public /* unsynchronized */ void processMoveMessage(Message.Move msg, Connection c) {

    }
}
