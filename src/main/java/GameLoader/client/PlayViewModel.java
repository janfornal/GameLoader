package GameLoader.client;

import static GameLoader.common.Serializables.*;
import GameLoader.common.Game;
import static GameLoader.common.Messages.*;

public interface PlayViewModel extends ViewModel {
    Game getGame();
    int playingAs();

    @Override
    default void setElements(GuiElements fooElements) {
    }

    default void processMoveMessage(MoveMessage msg) {
        Command cmd = msg.move();
        if (cmd.getPlayer() == playingAs())
            return;
        if (!getGame().isMoveLegal(cmd)) {
            getModelUser().sendError("this move is illegal?");
            return;
        }
        getGame().makeMove(cmd);
    }

    default void userCmd(Command cmd) {
        if (!getGame().isMoveLegal(cmd))
            return;

        getModelUser().sendMessage(new MoveMessage(cmd));
        getGame().makeMove(cmd);
    }
}
