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

public class PersonalStatistics {

    private Client user;
    private StringProperty playerName = new SimpleStringProperty();

    public final StringProperty titleTextProperty() {
        return this.playerName;
    }

    private HashMap<String, PersonalGameStats> personalGameControllers;  //keys represented games

    @FXML
    private AnchorPane personalStatistics;

    @FXML
    private Label titleLabel;

    @FXML
    void initialize() {
        System.out.println("hello");
        this.titleTextProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(titleLabel != null) {
                    titleLabel.setText("Statistics for " + newValue);
                }
            }
        });
        int position = 0;
        personalGameControllers = new HashMap<>();
        try {
            for(String s : user.gameTypeManager.getGameNames()) {
                PersonalGameStats personalGameStats = new PersonalGameStats();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(PersonalStatistics.class.getResource("/personalGameStats.fxml"));
                loader.setController(personalGameStats);
                Parent root = loader.load();
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

    public void passData(Client client, String player) {
        user = client;
        playerName.setValue(player);
    }
}
