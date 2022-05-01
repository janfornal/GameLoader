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

    @Override
    public void start(Stage stage) throws Exception {
        AuthorizationDialog startAuth = new AuthorizationDialog();
        String username = startAuth.getUsername();
        user.sendMessage(new Message.Authorization(username));
        MenuViewModel currentModel = new MenuViewModel(user);
        user.setCurrentModel(currentModel);
        view = new MenuView(currentModel);
        currentStage = stage;
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
