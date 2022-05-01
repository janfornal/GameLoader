package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Game;

public class SimpleTicTacToeViewModel implements PlayViewModel {
    public SimpleTicTacToeViewModel(Client user, SimpleTicTacToe game) {
        modelUser = user;
        modelGame = game;
    }

    private int prefWindowWidth = 600;
    private int prefWindowHeight = 400;
    private final Client modelUser;
    private final SimpleTicTacToe modelGame;

    public int getPrefWindowWidth() {
        return prefWindowWidth;
    }

    public int getPrefWindowHeight() {
        return prefWindowHeight;
    }

    private record guiElements() implements GuiElements {}

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public Game getGame() {
        return modelGame;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    public SimpleTicTacToeView createView() {
        return new SimpleTicTacToeView(this);
    }
}
