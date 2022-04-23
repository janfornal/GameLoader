package GameLoader.common.messages;

import java.util.List;

import GameLoader.client.Game;

public class GetGamesListMessage extends Message {
    List<Class<? extends Game>> list;
}
