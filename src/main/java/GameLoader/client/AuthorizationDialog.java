package GameLoader.client;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AuthorizationDialog { // TODO password field, cancel button is bugged
    private AtomicReference<String> username = new AtomicReference<>("");
    private TextInputDialog dialog;

    AuthorizationDialog() {
        dialog = new TextInputDialog();
        dialog.setTitle("Authorization");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
    }

    String getUsername() {
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username::set);
        return username.get();
    }
}
