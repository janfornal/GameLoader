package GameLoader.client;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Messages.*;

import GameLoader.common.Game;
import javafx.beans.binding.Bindings;
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

        int playingAs = ivm.playingAs();

        List<PlayerInfo> info = ivm.getPlayers();
        List<Text> names = playingAs == 0 ? List.of(user, opponent) : List.of(opponent, user);
        List<Text> elos = playingAs == 0 ? List.of(userElo, opponentElo) : List.of(opponentElo, userElo);

        for (int i = 0; i < 2; ++i) {
            elos.get(i).setText("Elo: " + info.get(i).elo());
            int finalI = i;
            names.get(i).textProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        if (ivm.getGame().getState() == Game.state.UNFINISHED && ivm.getGame().getTurn() == finalI)
                            return "> " + info.get(finalI).name() + " <";
                        else
                            return info.get(finalI).name();
                    }, ivm.getObservable()
            ));
        }

        String opponentName = info.get(1 - playingAs).name();
        ChatManager ch = ivm.getModelUser().chatManager;

        textArea.textProperty().bind(
                ch.get(opponentName)
        );

        textField.setOnAction(event -> {
            String txt = textField.getText();
            if (txt.length() == 0)
                return;
            ch.update(opponentName, ivm.getModelUser().username, txt);
            ivm.getModelUser().sendMessage(
                    new ChatMessageToServer(txt, opponentName)
            );
            textField.clear();
        });
    }

    public Node getMainNode() {
        return splitPane;
    }
}
