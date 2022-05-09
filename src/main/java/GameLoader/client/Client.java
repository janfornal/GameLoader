package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.DotsAndBoxes.*;
import GameLoader.games.SimpleTicTacToe.*;
import GameLoader.games.TicTacToe.TicTacToe;
import GameLoader.games.TicTacToe.TicTacToeView;
import GameLoader.games.TicTacToe.TicTacToeViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Client implements AbstractService {
    private MenuViewModel currentModel;
    private PlayViewModel currentPlayModel;
    private final Connection activeConnection;
    private RoomInfo chosenGame;
    public PlayerInfo username;
    private String availableGames;
    private final Map<String, GameClasses> gameMap = new HashMap<>() {{
        put(new DotsAndBoxes().getName(), new GameClasses(DotsAndBoxes.class, DotsAndBoxesView.class, DotsAndBoxesViewModel.class));
        put(new SimpleTicTacToe().getName(), new GameClasses(SimpleTicTacToe.class, SimpleTicTacToeView.class, SimpleTicTacToeViewModel.class));
        put(new TicTacToe().getName(),new GameClasses(TicTacToe.class,TicTacToeView.class, TicTacToeViewModel.class));
    }};

    private record GameClasses(Class<? extends Game> gameClass,
                               Class<? extends PlayView> gameViewClass,
                               Class<? extends PlayViewModel> gameModelClass) {}

    Client(String ip, int port) throws IOException {
        ClientGUI.user = this;
        activeConnection = new Connection(Client.this, ip, port);
        ClientGUI.launch(ClientGUI.class);
    }

    public void setCurrentModel(MenuViewModel viewModel) {currentModel = viewModel;}

    public void setCurrentPlayModel(PlayViewModel viewModel) {currentPlayModel = viewModel;}

    public void setChosenGame(RoomInfo game) {
        chosenGame = game;
    }

    void gameEnded() {
    }

    @Override
    public void processMessage(Message.Any message, Connection c) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(c);

        if(message instanceof Message.Authorization)
            ClientGUI.authorizationLockNotify();
        else if(message instanceof Message.RoomList messageCast) {
            System.out.println(FXCollections.observableArrayList(messageCast.rooms()));
            currentModel.getElements().roomTableView().setItems(FXCollections.observableArrayList(messageCast.rooms()));
        }
        else if(message instanceof Message.StartGame messageCast) {
            GameClasses gamePackage = gameMap.get(messageCast.game());
            Game starterInstance;
            PlayViewModel currentModelLocal;
            try {
                starterInstance = gamePackage.gameClass().getConstructor().newInstance();
                currentModelLocal = starterInstance.createViewModel(this, messageCast.p0().name().equals(username.name()) ? 0 : 1);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                sendMessage(new Message.Error("Constructor of game cannot be called"));
                return;
            }
            currentPlayModel = currentModelLocal;
            starterInstance.start(messageCast.settings(), messageCast.seed());
            Platform.runLater(
                    () -> ClientGUI.startNewTab(currentPlayModel)
            );
        }
        else if(message instanceof Message.Move messageCast) {
            Platform.runLater(
                    () -> currentPlayModel.processMoveMessage(messageCast)
            );
        }
        else if(message instanceof Message.Error messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, messageCast.cause()).showAndWait()
            );
            if(messageCast.cause().equals("Unsuccessful authorization")) {
                username = null;
                ClientGUI.authorizationLockNotify();
            }
        }
        else if(message instanceof Message.Resign messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, "Your opponent resigned").showAndWait()
            );
            currentPlayModel = null;
        }
        else c.sendError("Message not recognized");
    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void sendError(String cause) {
        sendMessage(new Message.Error(cause));
    }

    public void sendMessage(Message.Any message) {
        if(message instanceof Message.Authorization messageCast) {
            username = new PlayerInfo(messageCast.name());
        }
        activeConnection.sendMessage(message);
    }
}
