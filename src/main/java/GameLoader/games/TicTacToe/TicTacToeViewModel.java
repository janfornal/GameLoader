package GameLoader.games.TicTacToe;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import static GameLoader.common.Serializables.Command;
import static GameLoader.common.Messages.*;

public class TicTacToeViewModel implements PlayViewModel {
    public TicTacToeViewModel(Client user, int id, TicTacToe game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
    }

    @Override
    public TicTacToe getGame() {
        return modelGame;
    }

    private final Client modelUser;
    private final TicTacToe modelGame;
    private final int myPlayer;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public TicTacToeView createView() {
        return new TicTacToeView(this);
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
        Command cmd = new TicTacToeCommand(playingAs(), i, j);

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
