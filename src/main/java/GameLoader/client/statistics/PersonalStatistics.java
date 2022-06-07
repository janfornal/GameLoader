package GameLoader.client.statistics;

import GameLoader.common.Messages;
import GameLoader.common.Serializables;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.*;

import static GameLoader.client.statistics.StatisticsSingleton.playerName;
import static GameLoader.client.statistics.StatisticsSingleton.user;

public class PersonalStatistics {

    private HashMap<String, PersonalGameStats> personalGameControllers;  //keys represented games

    @FXML
    private AnchorPane personalStatistics;

    @FXML
    private Label titleLabel;

    @FXML
    void initialize() {
        setTitleLabel();
        personalGameControllers = new HashMap<>();
    }

    public void setBars() {
        personalStatistics.getChildren().removeIf(node -> node != titleLabel);
        personalGameControllers.clear();
        int position = 0;
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
            user.sendMessage(new Messages.QueryMessage(new Serializables.EloQuery(playerName, t.getKey())));
            user.sendMessage(new Messages.QueryMessage(new Serializables.GamesQuery(playerName, t.getKey())));
        }
    }

    public void setBar(String game, int won, int draw, int lost) {
        Platform.runLater(() -> {
            personalGameControllers.get(game).setBar(won, draw, lost);
        });
    }

    public void setElo(String game, int value) {
        personalGameControllers.get(game).setEloLabel(value);
    }

    public void setTitleLabel() {
        Platform.runLater(() -> {
            titleLabel.setText("Statistics for " + playerName);
        });
    }

}
