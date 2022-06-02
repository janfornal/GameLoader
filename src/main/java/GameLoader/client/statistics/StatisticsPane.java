package GameLoader.client.statistics;

import GameLoader.client.Client;
import GameLoader.common.GameTypeManager;
import GameLoader.common.Messages;
import GameLoader.common.Serializables;
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

import java.util.ArrayList;

public class StatisticsPane {

    @FXML
    private Button returnButton;

    @FXML
    private ComboBox<String> statisticsChoiceBox;

    @FXML
    private TableColumn<Pair<String, Integer>, Integer> statisticsColumn;

    @FXML
    private AnchorPane statisticsPane;

    @FXML
    private TableView<Pair<String, Integer>> statisticsTable;

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
        initializeComboBoxValues();
    }

    public void returnToMenu(ActionEvent actionEvent) {
    }

    private void initializeComboBoxValues() {
        statisticsChoiceBox.setItems(FXCollections.observableArrayList(StatisticSingleton.user.gameTypeManager.getGameNames()));
    }

    public void setItems(ArrayList<Pair<String, Integer>> itemList) {
        statisticsTable.setItems(FXCollections.observableArrayList(itemList));
    }

    public void queryStatistics(ActionEvent actionEvent) {
        StatisticSingleton.user.sendMessage(new Messages.QueryMessage(new Serializables.StatisticsQuery(StatisticSingleton.user.username, statisticsChoiceBox.getValue())));
    }
}
