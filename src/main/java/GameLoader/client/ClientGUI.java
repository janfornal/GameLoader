package GameLoader.client;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class ClientGUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Client clientInstance = new Client();
        Connection c = new Connection(clientInstance);
        AuthorizationDialog startAuth = new AuthorizationDialog();
        String username = startAuth.getUsername();
        c.sendMessage(new Message.Authorization(username));
        ViewModel currentModel = new MenuViewModel(c);
        MenuView view = new MenuView((MenuViewModel) currentModel);

        stage.setTitle("Let's Play");
        Scene scene = new Scene(view, ((MenuViewModel) currentModel).prefWindowWidth, ((MenuViewModel) currentModel).prefWindowHeight);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }
}
