package GameLoader.client;

import GameLoader.client.statistics.MainStatisticsWindow;
import GameLoader.client.statistics.StatisticSingleton;
import GameLoader.common.Game;
import static GameLoader.common.Serializables.ResignationCommand;
import GameLoader.games.chat.ChatWindow;
import static GameLoader.common.Messages.*;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


public class ClientGUI extends Application {

    private static Stage currentStage;
    static GeneralView view;
    public static Client user;
    static TabPane tabpane;
    public static MainStatisticsWindow mainStatisticsWindow;
    private static final Object authorizationLock = new Object();

    public static void authorizationLockNotify() {
        synchronized (authorizationLock) {
            authorizationLock.notify();
        }
    }

    public static <T extends Event> void HandlerFunction(T event) {
        Dialog<ButtonType> toQuit = new Dialog<>();
        toQuit.setTitle("Exit Server");
        toQuit.setContentText("Do you really want to exit server?");
        ButtonType type = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType type2 = new ButtonType("No", ButtonBar.ButtonData.NO);
        toQuit.getDialogPane().getButtonTypes().addAll(type, type2);
        Optional<ButtonType> closeResponse = toQuit.showAndWait();
        if(!type.equals(closeResponse.get()))
            event.consume();
        else {
            user.sendMessage(new EndConnectionMessage());
            if(currentStage != null) currentStage.close();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        synchronized (authorizationLock) {
            do {
                Stage authStage = new Stage();
                authStage.setOnCloseRequest(ClientGUI::HandlerFunction);
                AuthorizationDialog startAuth = new AuthorizationDialog();
                Message userData = startAuth.processAuthorization(authStage);
                if(userData == null) return;
                user.sendMessage(userData);
                authorizationLock.wait();
            } while (user.username == null);
        }
        MenuViewModel currentModel = new MenuViewModel(user);
        user.setCurrentModel(currentModel);
        view = new MenuView(currentModel);
        tabpane = new TabPane();
        Tab tab = new Tab("Menu");
        tab.setContent((Node) view);
        tabpane.getTabs().add(tab);
        currentStage = stage;
        user.sendMessage(new GetRoomListMessage());
        stage.setTitle("Game Server");
        Scene scene = new Scene(tabpane);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        tab.setOnCloseRequest(ClientGUI::HandlerFunction);
        stage.setOnCloseRequest(ClientGUI::HandlerFunction);
        stage.show();
    }

    public static void startNewTab (PlayViewModel viewModel, String opponentName) {
        view = viewModel.createView();
        Tab tab = new Tab(viewModel.getGame().getName() + " (with " + opponentName + ")");
        ChatWindow chatWindow = new ChatWindow(viewModel.getModelUser().username, viewModel.getModelUser());
        BorderPane bp = new BorderPane();
        bp.setCenter((Node) view);
        bp.setBottom(chatWindow);
        tab.setContent(bp);
        tab.setOnCloseRequest(e -> {
            if(viewModel.getGame().getState() != Game.state.UNFINISHED || viewModel.getModelUser().currentPlayModel != viewModel) {
                return;
            }
            Dialog<ButtonType> toQuit = new Dialog<>();
            toQuit.setTitle("Exit Game");
            toQuit.setContentText("Do you really want to end a game?");
            ButtonType type = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType type2 = new ButtonType("No", ButtonBar.ButtonData.NO);
            toQuit.getDialogPane().getButtonTypes().addAll(type, type2);
            Optional<ButtonType> closeResponse = toQuit.showAndWait();
            if(!type.equals(closeResponse.get())) {
                e.consume();
            }
            else user.sendMessage(new MoveMessage(new ResignationCommand(viewModel.playingAs())));
        });
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public static void startStatisticsTab(String playerName) {
        Tab tab = new Tab("Server statistics");
        tab.setOnCloseRequest(e -> {
            mainStatisticsWindow = null;
        });
        try {
            StatisticSingleton.user = user;
            StatisticSingleton.playerName = playerName;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientGUI.class.getResource("/mainStatisticsWindow.fxml"));
            Parent root = loader.load();
            tab.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

}
