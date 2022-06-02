package GameLoader.client.statistics;

import GameLoader.client.Client;
import GameLoader.common.Messages;
import GameLoader.common.Serializables;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class MainStatisticsWindow {

    private Client user;
    private String playerName;

    @FXML
    public PersonalStatistics personalStatisticsController;

    @FXML
    public StatisticsPane statisticsPaneController;

//    public MainStatisticsWindow(Client user, String playerName) {
//        this.user = user;
//        this.playerName = playerName;
//    }

    @FXML
    void initialize() {
//        try {
//            personalStatisticsController = new PersonalStatistics();
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(MainStatisticsWindow.class.getResource("/personalStatistics.fxml"));
//            loader.setController(personalStatisticsController);
//            Parent root = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            personalStatisticsController = new PersonalStatistics();
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(MainStatisticsWindow.class.getResource("/personalStatistics.fxml"));
//            loader.setController(personalStatisticsController);
//            Parent root = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void passData(Client client, String player) {
        user = client;
        playerName = player;
        statisticsPaneController.passData(client);
        personalStatisticsController.passData(client, player);
    }

}
