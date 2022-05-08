package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.client.ClientGUI;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.Message;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;

public class DotsAndBoxesViewModel implements PlayViewModel {
    public DotsAndBoxesViewModel(Client user, int id, DotsAndBoxes game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;

        modelGame.getGameStateProperty().addListener((a, b, c) -> {
            if(modelGame.getGameStateProperty().get() == Game.state.UNFINISHED) {// it can be shortened xd
                return;
            }
            modelUser.sendMessage(new Message.LeaveRoom());
        });
    }

    @Override
    public DotsAndBoxes getGame() {
        return modelGame;
    }

    private final Client modelUser;
    private final DotsAndBoxes modelGame;
    private final int myPlayer;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    @Override
    public DotsAndBoxesView createView() {
        return new DotsAndBoxesView(this);
    }

    @Override
    public void processMoveMessage(Message.Move msg) {
        Command cmd = msg.move();
        if (cmd.getPlayer() == myPlayer)
            return;
        if (!modelGame.isMoveLegal(cmd)) {
            modelUser.sendError("this move is illegal?");
            return;
        }
        modelGame.makeMove(cmd);
    }

    public void clickedOn(DotsAndBoxes.Coord c) {
        Command cmd = new DotsAndBoxesCommand(playingAs(), c);

        if (!modelGame.isMoveLegal(cmd))
            return;

        modelGame.makeMove(cmd);
        modelUser.sendMessage(new Message.Move(cmd));
    }

    public int playingAs() {
        return myPlayer;
    }
}
