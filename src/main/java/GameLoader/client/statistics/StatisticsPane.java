package GameLoader.client.statistics;

import GameLoader.client.Client;
import GameLoader.common.GameTypeManager;
import GameLoader.common.Messages;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

public class StatisticsPane {

    private Client user;

    @FXML
    private Button returnButton;

    @FXML
    private ComboBox<String> statisticsChoiceBox;

    @FXML
    private TableColumn<Pair<String, Integer>, Integer> statisticsColumn;

    @FXML
    private AnchorPane statisticsPane;

    @FXML
    TableView<Pair<String, Integer>> statisticsTable;

    @FXML
    private TableColumn<Pair<String, Integer>, String> userColumn;

    @FXML
    void initialize() {
        userColumn.setCellValueFactory(
                g -> new ReadOnlyObjectWrapper<String>(g.getValue().getKey())
        );

        statisticsColumn.setCellValueFactory(
                g -> new ReadOnlyObjectWrapper<Integer>(g.getValue().getValue())
        );
    }

    public void returnToMenu(ActionEvent actionEvent) {
    }

    public void passClientInstance(Client client) {
        user = client;
        initializeComboBoxValues();
    }

    private void initializeComboBoxValues() {
        statisticsChoiceBox.setItems(FXCollections.observableArrayList(user.gameTypeManager.getGameNames()));
    }

    public void queryStatistics(ActionEvent actionEvent) {
        user.sendMessage(new Messages.StatisticsQueryMessage(statisticsChoiceBox.getValue()));
    }
}
