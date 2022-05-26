package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import static GameLoader.common.Serializables.Command;
import static GameLoader.common.Messages.*;

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
    public void processMoveMessage(MoveMessage msg) {
        Command cmd = msg.move();
        if (cmd.getPlayer() == myPlayer)
            return;
        if (!modelGame.isMoveLegal(cmd)) {
            modelUser.sendError("this move is illegal?");
            return;
        }
        modelGame.makeMove(cmd);
    }

    public void clickedOn(int i, int j) {
        Command cmd = new SimpleTicTacToeCommand(playingAs(), i, j);

        if (!modelGame.isMoveLegal(cmd))
            return;

        modelUser.sendMessage(new MoveMessage(cmd));
        modelGame.makeMove(cmd);
    }

    @Override
    public int playingAs() {
        return myPlayer;
    }
}
