package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesView;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesViewModel;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToe;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToeView;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToeViewModel;
import GameLoader.server.GameManager;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client implements AbstractService {
    private ViewModel currentModel;
    private final Stage currentStage;
    private final Connection activeConnection;
    private RoomInfo chosenGame;
    public PlayerInfo username;
    private String availableGames;
    private final Map<String, GameClasses> gameMap = new HashMap<String, GameClasses>(){{
        put("Dots and Boxes", new GameClasses(DotsAndBoxes.class, DotsAndBoxesView.class, DotsAndBoxesViewModel.class));
        put("Tic Tac Toe", new GameClasses(SimpleTicTacToe.class, SimpleTicTacToeView.class, SimpleTicTacToeViewModel.class));
    }};

    private record GameClasses(Class <? extends Game> gameClass,
                               Class<? extends PlayView> gameViewClass,
                               Class<? extends PlayViewModel> gameModelClass) {}

    Client(Stage stage) throws IOException {
        currentStage = stage;
        activeConnection = new Connection(Client.this);
    }

    public void setCurrentModel(ViewModel Model) {
        currentModel = Model;
    }

    public void setChosenGame(RoomInfo game) {
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
                DotsAndBoxes starterInstance = new DotsAndBoxes();
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
