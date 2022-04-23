package GameLoader.common.messages;

import java.util.List;

import GameLoader.common.Game;

public class GetGamesListMessage extends Message {
    List<Class<? extends Game>> list;
}
