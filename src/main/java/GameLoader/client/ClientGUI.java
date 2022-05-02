package GameLoader.client;

import GameLoader.common.Message;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesView;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesViewModel;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.text.View;


public class ClientGUI extends Application {

    private static Stage currentStage;
    static GeneralView view;
    static Client user;
    private static final Object authorizationLock = new Object();

    public static void authorizationLockNotify() {
        synchronized (authorizationLock) {
            authorizationLock.notify();
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
        currentStage = stage;
        user.sendMessage(new Message.GetRoomList());
        stage.setTitle("Let's Play");
        Scene scene = new Scene((Parent) view);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }

    public static void switchStage (ViewModel viewModel) {
        view = viewModel.createView();
        currentStage.getScene().setRoot((Parent) view);
    }
}
