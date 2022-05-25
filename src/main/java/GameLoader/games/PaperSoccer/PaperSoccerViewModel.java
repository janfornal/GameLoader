package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.client.GeneralView;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Game;
import GameLoader.common.Message;

public class PaperSoccerViewModel implements PlayViewModel {
    @Override
    public Game getGame() {
        return null;
    }

    @Override
    public void processMoveMessage(Message.Move msg) {

    }

    @Override
    public int playingAs() {
        return 0;
    }

    @Override
    public Client getModelUser() {
        return null;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    @Override
    public GeneralView createView() {
        return null;
    }
}
