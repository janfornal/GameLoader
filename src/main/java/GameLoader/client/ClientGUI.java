package GameLoader.client;

import GameLoader.common.Game;
import GameLoader.common.Message;
import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;


public class ClientGUI extends Application {

    private static Stage currentStage;
    static GeneralView view;
    static Client user;
    static TabPane tabpane;
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
            user.sendMessage(new Message.EndConnection());
            currentStage.close();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        synchronized (authorizationLock) {
            do {
                AuthorizationDialog startAuth = new AuthorizationDialog();
                String username = startAuth.getUsername();
                user.sendMessage(new Message.Authorization(username));
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
        user.sendMessage(new Message.GetRoomList());
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
        tab.setContent((Node) view);
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
            else user.sendMessage(new Message.Resign());
        });
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

}
