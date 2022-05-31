package GameLoader.client.statistics;

import GameLoader.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.SegmentedBar;

import java.io.IOException;
import java.util.*;

public class PersonalStatistics {

    @FXML
    private AnchorPane personalStatistics;

    @FXML
    private Label titleLabel;

    private HashMap<String, PersonalGameStats> personalGameControllers;  //keys represented games
    private Client user;

    @FXML
    void initialize() {
        int position = 0;
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
            t.getValue().setGameName(t.getKey());
        }
    }

    public void passClientInstance(Client client) {
        user = client;
    }
}
