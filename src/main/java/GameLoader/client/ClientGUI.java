package GameLoader.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ClientGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MenuViewModel viewModel = new MenuViewModel(){};
        MenuView view = new MenuView(viewModel);

        stage.setTitle("Let's Play");
        Scene scene = new Scene(view, 600, 400);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }
}
