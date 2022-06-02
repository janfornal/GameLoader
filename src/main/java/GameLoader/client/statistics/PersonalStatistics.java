package GameLoader.client.statistics;

import GameLoader.client.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.*;

import static GameLoader.client.statistics.StatisticSingleton.playerName;
import static GameLoader.client.statistics.StatisticSingleton.user;

public class PersonalStatistics {

    private HashMap<String, PersonalGameStats> personalGameControllers;  //keys represented games

    @FXML
    private AnchorPane personalStatistics;

    @FXML
    private Label titleLabel;

    @FXML
    void initialize() {
        int position = 0;
        titleLabel.setText("Statistic for " + playerName);
        personalGameControllers = new HashMap<>();
        try {
            for(String s : user.gameTypeManager.getGameNames()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(PersonalStatistics.class.getResource("/personalGameStats.fxml"));
                Parent root = loader.load();
                PersonalGameStats personalGameStats = loader.getController();
                personalGameControllers.put(s, personalGameStats);
                personalStatistics.getChildren().add(root);
                AnchorPane.setTopAnchor(root, 100.0*position + 100.0);
                position++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Map.Entry<String, PersonalGameStats> t : personalGameControllers.entrySet()) {
            t.getValue().setGameLabel(t.getKey());
        }
    }

    public void setBar(String game, int won, int draw, int lost) {
        personalGameControllers.get(game).setBar(won, draw, lost);
    }

    public void setElo(String game, int value) {
        personalGameControllers.get(game).setEloLabel(value);
    }

}
