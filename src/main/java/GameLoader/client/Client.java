package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.DotsAndBoxes.*;
import GameLoader.games.SimpleTicTacToe.*;
import GameLoader.games.TicTacToe.TicTacToe;
import GameLoader.games.TicTacToe.TicTacToeView;
import GameLoader.games.TicTacToe.TicTacToeViewModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Client implements Service {
    private MenuViewModel currentModel;
    public PlayViewModel currentPlayModel;
    private final Connection activeConnection;
    private SimpleObjectProperty<Message.ChatMessage> messageProperty = null;
    private RoomInfo chosenGame;
    public String username;
    public final Map<String, GameClasses> gameMap = new HashMap<>() {{
        put(new DotsAndBoxes().getName(), new GameClasses(DotsAndBoxes.class, DotsAndBoxesView.class, DotsAndBoxesViewModel.class));
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

    public void setChosenGame(RoomInfo game) {
        chosenGame = game;
    }

    public List<String> getGameSettings(String game) {  /// reflection used
        try {
            Game helperGameObject = gameMap.get(game).gameClass.getConstructor().newInstance();
            return helperGameObject.possibleSettings();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            System.err.println("You want to get settings list of game, which doesn't exist!");
        }
        return null;
    }

    void gameEnded() {
    }

    @Override
    public void processMessage(Message.Any message, Connection c) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(c);

        if(message instanceof Message.SuccessfulAuthorization)
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
                currentModelLocal = starterInstance.createViewModel(this, messageCast.p0().name().equals(username) ? 0 : 1);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                sendMessage(new Message.Error("Constructor of game cannot be called"));
                return;
            }
            currentPlayModel = currentModelLocal;
            starterInstance.start(messageCast.settings(), messageCast.seed());
            String [] playerNames = new String[]{messageCast.p0().name(), messageCast.p1().name()};
            Platform.runLater(
                    () -> ClientGUI.startNewTab(currentPlayModel, playerNames[0].equals(username) ? playerNames[1] : playerNames[0])
            );
        }
        else if(message instanceof Message.ChatMessage messageCast) {
            messageProperty.set(messageCast);
        }
        else if(message instanceof Message.Move messageCast) {
            if (messageCast.move() instanceof ResignationCommand res && currentPlayModel.playingAs() != res.getPlayer())
                Platform.runLater(
                        () -> new Alert(Alert.AlertType.ERROR, "Your opponent resigned").showAndWait()
                );

            Platform.runLater(
                    () -> currentPlayModel.processMoveMessage(messageCast)
            );
        }
        else if(message instanceof Message.Error messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, messageCast.cause()).showAndWait()
            );
        }
        else if(message instanceof Message.UnsuccessfulAuthorization messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, messageCast.cause()).showAndWait()
            );
            username = null;
            ClientGUI.authorizationLockNotify();
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
        if(message instanceof Message.AuthorizationAttempt messageCast) {
            username = messageCast.name();
        }
        if(message instanceof Message.RegistrationAttempt messageCast) {
            username = messageCast.name();
        }
        activeConnection.sendMessage(message);
    }

    public SimpleObjectProperty<Message.ChatMessage> getMessageProperty() {   // why am I here?
        if (messageProperty == null)
            messageProperty = new SimpleObjectProperty<Message.ChatMessage>(new Message.ChatMessage(""));
        return messageProperty;
    }
}
