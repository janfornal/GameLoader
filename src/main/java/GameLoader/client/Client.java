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
    private SimpleObjectProperty<ChatMessage> messageProperty = null;
    public String username;

    public Client(String ip, int port) throws IOException {
        ClientGUI.user = this;
        activeConnection = new Connection(Client.this, ip, port);
        ClientGUI.launch(ClientGUI.class);
    }

    public void setCurrentModel(MenuViewModel viewModel) {currentModel = viewModel;}

    void gameEnded() {
    }

    @Override
    public void processMessage(AnyMessage message, Connection c) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(c);

        if(message instanceof SuccessfulAuthorizationMessage)
            ClientGUI.authorizationLockNotify();
        else if(message instanceof RoomListMessage messageCast) {
            System.out.println(FXCollections.observableArrayList(messageCast.rooms()));
            currentModel.getElements().roomTableView().setItems(FXCollections.observableArrayList(messageCast.rooms()));
        }
        else if(message instanceof StartGameMessage messageCast) {
            Game starterInstance = gameTypeManager.createGame(messageCast.game(), messageCast.settings());

            if (starterInstance == null) {
                sendMessage(new ErrorMessage("Constructor of game cannot be called"));
                return;
            }

            starterInstance.start(messageCast.settings(), messageCast.seed());
            currentPlayModel = starterInstance.createViewModel(this, messageCast.p0().name().equals(username) ? 0 : 1);
            String [] playerNames = new String[]{messageCast.p0().name(), messageCast.p1().name()};
            Platform.runLater(
                    () -> ClientGUI.startNewTab(currentPlayModel, playerNames[0].equals(username) ? playerNames[1] : playerNames[0])
            );
        }
        else if(message instanceof ChatMessage messageCast) {
            messageProperty.set(messageCast);
        }
        else if(message instanceof MoveMessage messageCast) {
            if (messageCast.move() instanceof ResignationCommand res && currentPlayModel.playingAs() != res.getPlayer())
                Platform.runLater(
                        () -> new Alert(Alert.AlertType.ERROR, "Your opponent resigned").showAndWait()
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
        else c.sendError("Message not recognized");
    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void sendError(String cause) {
        sendMessage(new ErrorMessage(cause));
    }

    public void sendMessage(AnyMessage message) {
        if(message instanceof AuthorizationAttemptMessage messageCast) {
            username = messageCast.name();
        }
        if(message instanceof RegistrationAttemptMessage messageCast) {
            username = messageCast.name();
        }
        activeConnection.sendMessage(message);
    }

    public SimpleObjectProperty<ChatMessage> getMessageProperty() {   // why am I here?
        if (messageProperty == null)
            messageProperty = new SimpleObjectProperty<ChatMessage>(new ChatMessage(""));
        return messageProperty;
    }

}
