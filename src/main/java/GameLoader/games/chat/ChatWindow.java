package GameLoader.games.chat;

import GameLoader.client.Client;
import GameLoader.common.Message;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class ChatWindow extends VBox {
    private final Button openBtn;
    private final AppriopriateWindow chatWindow;
    private final SimpleObjectProperty<Message.ChatMessage> obs;

    private void insertChat() {
        getChildren().add(chatWindow);
    }

    private void removeChat() {
        getChildren().remove(chatWindow);
    }

    public ChatWindow(String username, Client user) {
        obs = user.getMessageProperty();
        obs.addListener((a, b, c) -> {});
        chatWindow = new AppriopriateWindow(username, user);
        openBtn = new Button("Open");
        openBtn.setAlignment(Pos.BOTTOM_RIGHT);
        openBtn.setOnAction(event -> {
            String newText = openBtn.getText().equals("Close") ? "Open" : "Close";
            openBtn.setText(newText);
            if(newText.equals("Close")) {
                ScaleTransition trans = new ScaleTransition(Duration.millis(150), chatWindow);
                trans.setFromY(0.0);
                trans.setToY(1.0);
                trans.play();
                insertChat();
            }
            if(newText.equals("Open")) {
                ScaleTransition trans = new ScaleTransition(Duration.millis(150), chatWindow);
                trans.setFromY(1.0);
                trans.setToY(0.0);
                trans.play();
                removeChat();
            }
        });
        getChildren().add(openBtn);
    }

    public class AppriopriateWindow extends VBox {
        private final String username;
        private final Client user;
        private final TextArea messageArea = new TextArea();
        private final TextField input = new TextField();
        public AppriopriateWindow(String username, Client user) {
            this.username = username;
            this.user = user;
            messageArea.setFont(Font.font(12));
            messageArea.setPrefHeight(150);
            messageArea.setEditable(false);
            System.out.println(messageArea.getText());
            input.setOnAction(event -> {
                String message = this.username + ": " + input.getText() + "\n";
                input.clear();
                user.sendMessage(new Message.ChatMessage(message));
            });
            messageArea.textProperty().bind(Bindings.createObjectBinding(
                    () -> messageArea.getText() + obs.get().text(),
                    obs
            ));
            this.getChildren().addAll(messageArea, input);
        }
    }

}
