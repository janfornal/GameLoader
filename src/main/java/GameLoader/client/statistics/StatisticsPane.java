package GameLoader.client.statistics;

import GameLoader.client.ClientGUI;
import GameLoader.common.Messages;
import GameLoader.common.Serializables;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;

import static GameLoader.client.ClientGUI.mainStatisticsWindow;

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
        statisticsTable.setRowFactory(tv -> {
            TableRow<Pair<String, Integer>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Pair<String, Integer> rowData = row.getItem();
                    mainStatisticsWindow.reload(rowData.getKey());
                }
            });
            return row;
        });
    }

    public void returnToMenu(ActionEvent actionEvent) {
        ClientGUI.closeStatisticsTab();
    }

    private void initializeComboBoxValues() {
        statisticsChoiceBox.setItems(FXCollections.observableArrayList(StatisticsSingleton.user.gameTypeManager.getGameNames()));
    }

    public void setItems(ArrayList<Pair<String, Integer>> itemList) {
        statisticsTable.setItems(FXCollections.observableArrayList(itemList));
        statisticsTable.setSortPolicy(t -> {
            Comparator<Pair<String, Integer>> comparator = (r1, r2)
                    -> !r1.getValue().equals(r2.getValue()) ? Integer.compare(r2.getValue(), r1.getValue())
                    : CharSequence.compare(r1.getKey(), r2.getKey());
            FXCollections.sort(statisticsTable.getItems(), comparator);
            return true;
        });
    }

    public void queryStatistics(ActionEvent actionEvent) {
        StatisticsSingleton.user.sendMessage(new Messages.QueryMessage(new Serializables.StatisticsQuery(StatisticsSingleton.user.username, statisticsChoiceBox.getValue())));
    }
}
