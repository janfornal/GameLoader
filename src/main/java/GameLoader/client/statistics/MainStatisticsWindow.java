package GameLoader.client.statistics;

import GameLoader.client.Client;
import GameLoader.common.Messages;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class MainStatisticsWindow {

    Client user;

    @FXML
    private PersonalStatistics personalStatisticsController;

    @FXML
    private StatisticsPane statisticsPaneController;

    public void passClientInstance(Client client) {
        user = client;
        statisticsPaneController.passClientInstance(client);
        personalStatisticsController.passClientInstance(client);
    }

    public void showStatistics(Messages.StatisticsDatabaseMessage msg) {
        statisticsPaneController.statisticsTable.setItems(FXCollections.observableArrayList(msg.eloList()));
    }
}
