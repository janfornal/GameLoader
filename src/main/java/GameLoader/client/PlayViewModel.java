package GameLoader.client;

import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.Message;

public interface PlayViewModel extends ViewModel {
    Game getGame();
    int playingAs();

    @Override
    default void setElements(GuiElements fooElements) {
    }

    default void processMoveMessage(Message.Move msg) {
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

        getModelUser().sendMessage(new Message.Move(cmd));
        getGame().makeMove(cmd);
    }
}
