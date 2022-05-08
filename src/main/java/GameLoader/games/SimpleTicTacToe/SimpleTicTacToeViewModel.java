package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.client.ClientGUI;
import GameLoader.client.GuiElements;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.Message;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;

public class SimpleTicTacToeViewModel implements PlayViewModel {
    public SimpleTicTacToeViewModel(Client user, int id, SimpleTicTacToe game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;

        // TODO delete this
        modelGame.getMoveCountProperty().addListener((a, b, c) -> {
            if (game.getState() == Game.state.UNFINISHED)
                return;

            modelUser.execNormal.execute(()-> {
                try {
                    TimeUnit.MILLISECONDS.sleep(3333);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(ClientGUI::reset);
            });
        });
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
            modelUser.sendError("this move is illegal?");
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
