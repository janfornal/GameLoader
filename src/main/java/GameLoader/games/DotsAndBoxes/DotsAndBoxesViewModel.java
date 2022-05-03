package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Message;

public class DotsAndBoxesViewModel implements PlayViewModel {
    public DotsAndBoxesViewModel(Client user, int id, DotsAndBoxes game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
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
        System.err.println("Clicked on " + c);

        Command cmd = new DotsAndBoxesCommand(playingAs(), c);

        if (!modelGame.isMoveLegal(cmd)) {
            System.err.println("THIS IS ILLEGAL");
            System.err.println(getGame());
        }

        modelGame.makeMove(cmd);
        modelUser.sendMessage(new Message.Move(cmd));
    }

    public int playingAs() {
        return myPlayer;
    }
}
