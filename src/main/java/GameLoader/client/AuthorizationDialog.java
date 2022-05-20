package GameLoader.client;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AuthorizationDialog extends SplitPane {

    private AtomicReference<String> username = new AtomicReference<>("");
    private AnchorPane leftPane;
    private Label changeAuthorizationDialog;
    private Button changeAuthorizationButton;
    private AnchorPane rightPane;
    private Label titleLabel;
    private GridPane dataGridPane;
    private TextField usernameTextField;
    private PasswordField passwordTextField;
    private Button confirmationButton;
    private boolean isRegister;
    private Stage parentStage;

    AuthorizationDialog() {
        leftPane = new AnchorPane();
        leftPane.setPrefHeight(298.4);
        leftPane.setPrefWidth(221.6);
        rightPane = new AnchorPane();
        rightPane.setPrefHeight(298.4);
        rightPane.setPrefWidth(221.6);
        getItems().addAll(leftPane, rightPane);
        changeAuthorizationDialog = new Label("Don't have an account?");
        changeAuthorizationButton = new Button("Sign up");
        leftPane.getChildren().addAll(changeAuthorizationDialog, changeAuthorizationButton);
        AnchorPane.setLeftAnchor(changeAuthorizationDialog, 13.0);
        AnchorPane.setTopAnchor(changeAuthorizationDialog, 92.0);
        changeAuthorizationDialog.setFont(Font.font(16));
        AnchorPane.setLeftAnchor(changeAuthorizationButton, 13.0);
        AnchorPane.setTopAnchor(changeAuthorizationButton, 137.0);
        changeAuthorizationButton.setOnMouseClicked(mouseEvent -> {
            if(isRegister) {
                changeAuthorizationDialog.setText("Don't have an account?");
                changeAuthorizationButton.setText("Sign up");
                titleLabel.setText("Login");
            }
            else {
                changeAuthorizationDialog.setText("Have an account?");
                changeAuthorizationButton.setText("Sign in");
                titleLabel.setText("Register");
            }
            isRegister = !isRegister;
        });
        titleLabel = new Label("Login");
        dataGridPane = new GridPane();
        confirmationButton = new Button("OK");
        confirmationButton.setOnMouseClicked(mouseEvent -> {
            if(usernameTextField.getText().equals("") || passwordTextField.getText().equals("")) {
                usernameTextField.setPromptText("Enter your username");
                passwordTextField.setPromptText("Enter your password");
                return;
            }
            parentStage.hide();
        });
        rightPane.getChildren().addAll(titleLabel, dataGridPane, confirmationButton);
        AnchorPane.setLeftAnchor(titleLabel, 13.0);
        AnchorPane.setTopAnchor(titleLabel, 79.0);
        AnchorPane.setLeftAnchor(dataGridPane, 13.0);
        AnchorPane.setRightAnchor(dataGridPane, 13.0);
        AnchorPane.setTopAnchor(dataGridPane, 119.0);
        AnchorPane.setRightAnchor(confirmationButton, 13.0);
        AnchorPane.setBottomAnchor(confirmationButton, 14.0);
        titleLabel.setFont(Font.font(16));
        List<Integer> columnWidth = Arrays.asList(196);
        dataGridPane.getColumnConstraints().addAll(columnWidth.stream()
                .map(t -> new ColumnConstraints()).toList());
        List<Integer> rowHeight = Arrays.asList(30, 30);
        dataGridPane.getRowConstraints().addAll(rowHeight.stream()
                .map(t -> new RowConstraints()).toList());
        usernameTextField = new TextField();
        usernameTextField.setPrefSize(196.0, 25.6);
        dataGridPane.add(usernameTextField, 0, 0, 1, 1);
        usernameTextField.setPromptText("Enter your username");
        passwordTextField = new PasswordField();
        passwordTextField.setPrefSize(196.0, 25.6);
        dataGridPane.add(passwordTextField, 0, 1, 1, 1);
        passwordTextField.setPromptText("Enter your password");
    }

    void processAuthorization(Stage stage) {
        parentStage = stage;
        stage.setScene(new Scene(this));
        stage.setResizable(false);
        stage.showAndWait();

    }
}
