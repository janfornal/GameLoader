package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.client.GeneralView;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.Message;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DotsAndBoxesViewModel implements PlayViewModel {

    public DotsAndBoxesViewModel(Client user, DotsAndBoxes game) {
        modelUser = user;
        modelGame = game;
    }

    public guiElements getElements() {
        return guiVisual;
    }

    @Override
    public Game getGame() {
        return modelGame;
    }

    @Override
    public void processMoveMessage(Message.Move msg) {
        Command cmd = msg.move();

    }

    record guiElements (
            Label stateOfGame,
            Label ourScore,
            Label enemyScore,
            GridPane board
    ) implements GuiElements { }

    guiElements guiVisual;
    private final Client modelUser;
    private final Game modelGame;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    @Override
    public GeneralView createView() {
        return new DotsAndBoxesView(this);
    }
}
