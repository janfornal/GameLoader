package GameLoader.client;

import GameLoader.common.Game;
import GameLoader.common.Message;

public interface PlayViewModel extends ViewModel {
    Game getGame();
    void processMoveMessage(Message.Move msg);
    int playingAs();
}
