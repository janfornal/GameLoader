package GameLoader.common.messages;

import java.util.List;

import GameLoader.client.Game;

public class GetGamesListMessage implements Message {
    List<Class<? extends Game>> list;
}
