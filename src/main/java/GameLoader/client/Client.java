package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.Game;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.IOException;

public class Client implements AbstractService {
    private ViewModel currentModel;
    private final Stage currentStage;
    private final Connection activeConnection;
    private Game.GameInfo chosenGame;
    public PlayerInfo username;

    Client(Stage stage) throws IOException {
        currentStage = stage;
        activeConnection = new Connection(Client.this);
    }

    public void setCurrentModel(ViewModel Model) {
        currentModel = Model;
    }

    public void setChosenGame(Game.GameInfo game) {
        chosenGame = game;
    }

    void gameEnded() {
    }

    @Override
    public void processMessage(Message.Any message, Connection c) {
        if(message instanceof Message.Authorization messageCast)
            return;
        if(message instanceof Message.RoomList messageCast && currentModel instanceof MenuViewModel currentGameCast)
            currentGameCast.getElements().roomTableView().setItems(FXCollections.observableArrayList(messageCast.rooms()));
        if(message instanceof Message.StartGame && currentModel instanceof MenuViewModel currentGameCast) {
            if(chosenGame.equals("Dots and Boxes")) {
                DotsAndBoxes starterInstance = new DotsAndBoxes().createNewGame();
                currentModel = new DotsAndBoxesViewModel(this, starterInstance);
            }
            if(chosenGame.equals("Tic Tac Toe")) {
                // repeat above;
            }
            ClientGUI.switchStage(currentStage, currentModel);
        }
    }

    @Override
    public void reportGameEnded(Game gm) {

    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void sendMessage(Message.Any message) {
        activeConnection.sendMessage(message);
        if(message instanceof Message.Authorization authMessage) {
            username = new PlayerInfo(authMessage.name());
        }
    }
}
