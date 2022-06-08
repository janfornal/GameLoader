package GameLoader.client;

import GameLoader.common.*;
import static GameLoader.common.Serializables.ResignationCommand;
import static GameLoader.common.Messages.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.*;

public class Client implements Service {
    private MenuViewModel currentModel;
    public PlayViewModel currentPlayModel;
    private final Connection activeConnection;
    public final ChatManager chatManager = new ChatManager();
    public String username;

    public Client(String ip, int port) {
        ClientGUI.user = this;
        Connection tmp = null;
        try {
            tmp = new Connection(this, ip, port);
        } catch (IOException e) {
            throw new RuntimeException("Connection attempt to ip:" + ip + ", port:" + port + " was unsuccessful.", e);
        }
        activeConnection = tmp;
        ClientGUI.launch(ClientGUI.class);
    }

    public void setCurrentModel(MenuViewModel viewModel) {currentModel = viewModel;}

    void gameEnded() {
    }

    @Override
    public void processMessage(Message message, Connection c) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(c);

        if(message instanceof SuccessfulAuthorizationMessage)
            ClientGUI.authorizationLockNotify();
        else if(message instanceof RoomListMessage messageCast) {
            Platform.runLater(
                    () -> currentModel.getElements().roomTableView().setItems(FXCollections.observableArrayList(messageCast.rooms()))
            );
        }
        else if(message instanceof StartGameMessage messageCast) {
            Game starterInstance = gameTypeManager.createGame(messageCast.game(), messageCast.settings());

            if (starterInstance == null) {
                sendMessage(new ErrorMessage("Constructor of game cannot be called"));
                return;
            }

            starterInstance.start(messageCast.settings(), messageCast.seed());
            currentPlayModel = new InfoViewModel(
                    starterInstance.createViewModel(this, messageCast.p0().name().equals(username) ? 0 : 1),
                    messageCast.p0(), messageCast.p1()
            );
            String [] playerNames = new String[]{messageCast.p0().name(), messageCast.p1().name()};
            Platform.runLater(
                    () -> ClientGUI.startNewTab(currentPlayModel, playerNames[0].equals(username) ? playerNames[1] : playerNames[0])
            );
        }
        else if(message instanceof ChatMessageToClient messageCast) {
            Platform.runLater(
                    () -> chatManager.update(messageCast.sender(), messageCast.sender(), messageCast.text())
            );
        }
        else if(message instanceof MoveMessage messageCast) {
            if (messageCast.move() instanceof ResignationCommand res && currentPlayModel.playingAs() != res.getPlayer())
                Platform.runLater(
                        () -> new Alert(Alert.AlertType.INFORMATION, "Your opponent resigned").showAndWait()
                );

            Platform.runLater(
                    () -> currentPlayModel.processMoveMessage(messageCast)
            );
        }
        else if(message instanceof ErrorMessage messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, messageCast.cause()).showAndWait()
            );
        }
        else if(message instanceof UnsuccessfulAuthorizationMessage messageCast) {
            Platform.runLater(
                    () -> new Alert(Alert.AlertType.ERROR, messageCast.cause()).showAndWait()
            );
            username = null;
            ClientGUI.authorizationLockNotify();
        }
        else if(message instanceof AnswerMessage messageCast) {
            Platform.runLater(
                    () -> {
                        if(ClientGUI.mainStatisticsWindow == null) c.sendError("Statistics menu is closed");
                        Serializables.DatabaseAnswer ans = messageCast.answer();
                        if(ans == null) c.sendError("Got null database query answer");
                        if(ans instanceof Serializables.EloAnswer eloans) {
                            ClientGUI.mainStatisticsWindow.personalStatisticsController.setElo(eloans.game(), eloans.value());
                        }
                        if(ans instanceof Serializables.StatisticsAnswer statans) {
                            ClientGUI.mainStatisticsWindow.statisticsPaneController.setItems(statans.eloList());
                        }
                        if(ans instanceof Serializables.GamesAnswer gameans) {
                            ClientGUI.mainStatisticsWindow.personalStatisticsController.setBar(gameans.game(), gameans.won(), gameans.draw(), gameans.lost());
                        }
                    }
            );
        }
        else c.sendError("Message not recognized");
    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void sendError(String cause) {
        sendMessage(new ErrorMessage(cause));
    }

    public void sendMessage(Message message) {
        if(message instanceof AuthorizationAttemptMessage messageCast) {
            username = messageCast.name();
        }
        if(message instanceof RegistrationAttemptMessage messageCast) {
            username = messageCast.name();
        }
        activeConnection.sendMessage(message);
    }
}
