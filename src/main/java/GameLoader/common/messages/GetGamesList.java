package GameLoader.common.messages;

import java.util.List;
import GameLoader.client.Game;
public class GetGamesList implements Message{
    List<Class<? extends Game>> list;
}
