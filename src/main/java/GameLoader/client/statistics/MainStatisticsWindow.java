package GameLoader.client.statistics;

import GameLoader.client.ClientGUI;
import javafx.fxml.FXML;

public class MainStatisticsWindow {

    @FXML
    public PersonalStatistics personalStatisticsController;

    @FXML
    public StatisticsPane statisticsPaneController;

    @FXML
    void initialize() {
        ClientGUI.mainStatisticsWindow = this;
        personalStatisticsController.setBars();
    }

    public void reload(String playerName) {
        StatisticsSingleton.playerName = playerName;
        personalStatisticsController.setTitleLabel();
        personalStatisticsController.setBars();
    }
}
