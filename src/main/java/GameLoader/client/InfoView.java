package GameLoader.client;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Messages.*;

import GameLoader.common.Messages;
import GameLoader.common.UtilityGUI;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.List;

public class InfoView {
    private final InfoViewModel ivm;
    public InfoView(InfoViewModel infoViewModel) {
        ivm = infoViewModel;
    }

    @FXML
    private SplitPane splitPane;

    @FXML
    private Text user, userElo, opponent, opponentElo;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;

    @FXML
    public void initialize() {
        double divide = splitPane.getDividerPositions()[0];
        splitPane.getItems().set(0, ivm.createGameView());
        splitPane.setDividerPosition(0, divide);

        PlayerInfo userInfo = ivm.getPlayers().get(ivm.playingAs());
        user.setText(userInfo.name());
        userElo.setText("Elo: " + userInfo.elo());

        PlayerInfo opponentInfo = ivm.getPlayers().get(1 - ivm.playingAs());
        opponent.setText(opponentInfo.name());
        opponentElo.setText("Elo: " + opponentInfo.elo());

        ChatManager ch = ivm.getModelUser().chatManager;

        textArea.textProperty().bind(
                ch.get(opponentInfo.name())
        );

        textField.setOnAction(event -> {
            String txt = textField.getText();
            if (txt.length() == 0)
                return;
            ch.update(opponentInfo.name(), ivm.getModelUser().username, txt);
            ivm.getModelUser().sendMessage(
                    new ChatMessageToServer(txt, opponentInfo.name())
            );
            textField.clear();
        });
    }

    public Node getMainNode() {
        return splitPane;
    }
}
