package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Message;

public class SimpleTicTacToeViewModel implements PlayViewModel {
    public SimpleTicTacToeViewModel(Client user, int id, SimpleTicTacToe game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
    }

    @Override
    public SimpleTicTacToe getGame() {
        return modelGame;
    }

    private final Client modelUser;
    private final SimpleTicTacToe modelGame;
    private final int myPlayer;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {

    }

    @Override
    public SimpleTicTacToeView createView() {
        return new SimpleTicTacToeView(this);
    }

    @Override
    public void processMoveMessage(Message.Move msg) {
        Command cmd = msg.move();
        if (cmd.getPlayer() == myPlayer)
            return;
        if (!modelGame.isMoveLegal(cmd)) {
            modelUser.sendMessage(new Message.Error("this move is illegal?"));
            return;
        }
        modelGame.makeMove(cmd);
    }

    public void clickedOn(int i, int j) {
        Command cmd = new SimpleTicTacToeCommand(playingAs(), i, j);

        if (!modelGame.isMoveLegal(cmd))
            return;

        modelGame.makeMove(cmd);
        modelUser.sendMessage(new Message.Move(cmd));
    }

    public int playingAs() {
        return myPlayer;
    }
}
