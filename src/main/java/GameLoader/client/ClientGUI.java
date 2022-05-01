package GameLoader.client;

import GameLoader.common.Message;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesView;
import GameLoader.games.DotsAndBoxes.DotsAndBoxesViewModel;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ClientGUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Client clientInstance = new Client(stage);
        AuthorizationDialog startAuth = new AuthorizationDialog();
        String username = startAuth.getUsername();
        clientInstance.sendMessage(new Message.Authorization(username));
        MenuViewModel currentModel = new MenuViewModel(clientInstance);
        clientInstance.setCurrentModel(currentModel);
        MenuView view = new MenuView(currentModel);

        stage.setTitle("Let's Play");
        Scene scene = new Scene(view, currentModel.prefWindowWidth, currentModel.prefWindowHeight);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }

    public static void switchStage (Stage stage, ViewModel viewModel) {
        if(viewModel instanceof DotsAndBoxesViewModel dotsViewModel) {
            DotsAndBoxesView view = new DotsAndBoxesView(dotsViewModel);
            Scene scene = new Scene(view, dotsViewModel.prefWindowWidth, dotsViewModel.prefWindowHeight);
            stage.setScene(scene);
        }
        stage.show();
    }
}
