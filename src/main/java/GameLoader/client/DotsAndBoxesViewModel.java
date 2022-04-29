package GameLoader.client;

import GameLoader.games.Game;
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

    record guiElements (
            Label stateOfGame,
            Label ourScore,
            Label enemyScore,
            GridPane board
    ) implements GuiElements { }

    guiElements guiVisual;
    int prefWindowWidth = 600;
    int prefWindowHeight = 400;
    private final Client modelUser;
    private final Game modelGame;
    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }
}
