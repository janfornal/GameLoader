package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Connection;
import GameLoader.common.Game;
import GameLoader.common.Message;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SimpleTicTacToeViewModel implements PlayViewModel {
    public SimpleTicTacToeViewModel(Client user, int id, SimpleTicTacToe game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
    }

    public guiElements getElements() {
        return guiVisual;
    }

    @Override
    public SimpleTicTacToe getGame() {
        return modelGame;
    }

    record guiElements (
            Label stateOfGame,
            GridPane board
    ) implements GuiElements { }

    guiElements guiVisual;
    public int prefWindowWidth = 600;
    public int prefWindowHeight = 400;
    private final Client modelUser;
    private final SimpleTicTacToe modelGame;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    public void processMoveMessage(Message.Move msg, Connection c) {
        Command cmd = msg.move();
        if (cmd.getPlayer() == myPlayer)
            return;
        if (!modelGame.isMoveLegal(cmd)) {
            c.sendError("this move is illegal?");
            return;
        }
        modelGame.makeMove(cmd);
        state = modelGame.getState();
    }

    public void clickedOn(int i, int j) {
        System.err.println("Clicked on " + i + ", " + j);

        Command cmd = new SimpleTicTacToeCommand(playingAs(), i, j);

        if (!modelGame.isMoveLegal(cmd))
            return;

        modelGame.makeMove(cmd);
        modelUser.sendMessage(new Message.Move(cmd));
    }

    private Game.state state = Game.state.UNFINISHED;
    private final int myPlayer;

    public int playingAs() {
        return myPlayer;
    }

    public boolean finished() {
        return state != Game.state.UNFINISHED;
    }
}
