package GameLoader.client;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuView extends GridPane implements GeneralView {
    public static class Room {
        private final SimpleStringProperty game;
        private final SimpleStringProperty size;
        private final SimpleIntegerProperty user;

        private Room(String game, String size, int user) {
            this.game = new SimpleStringProperty(game);
            this.size = new SimpleStringProperty(size);
            this.user = new SimpleIntegerProperty(user);
        }

        public String getGame() {
            return game.get();
        }

        public void setGame(String gName) {
            game.set(gName);
        }

        public String getSize() {
            return size.get();
        }

        public void setSize(String sName) {
            size.set(sName);
        }

        public int getUser() {
            return user.get();
        }

        public void setUser(int uName) {
            user.set(uName);
        }
    }

    private ChoiceBox<?> choiceGameBox;
    private ChoiceBox<?> choiceSizeBox;
    private Label createRoomLabel;
    private TableView<Room> roomTableView;
    private Button createRoomButton;
    private TableColumn<Room, String> gameColumn;
    private TableColumn<Room, String> sizeColumn;
    private TableColumn<Room, Integer> userColumn;

    int prefWindowWidth = 600;
    int prefWindowHeight = 400;

    private final ObservableList<Room> data =
            FXCollections.observableArrayList(
                    new Room("Dots and boxes", "Small", 2819),
                    new Room("Tic tac toe", "Big", 1782),
                    new Room("Tic tac toe", "Medium", 2144),
                    new Room("Dots and boxes", "Big", 839),
                    new Room("Tic tac toe", "Big", 1012)
            );

    public MenuView(MenuViewModel nvm) {

        setPrefSize(prefWindowWidth, prefWindowHeight);

        setPadding(new Insets(10, 20, 10, 20));

        List<Integer> columnWidth = Arrays.asList(225, 75, 130, 150);
        getColumnConstraints().addAll(columnWidth.stream()
                .map(t -> new ColumnConstraints(t)).toList());

        for(int i=0; i<columnWidth.size(); i++) {
            getColumnConstraints().get(i).setPercentWidth(columnWidth.get(i));
        }

        List <Integer> rowHeight = Arrays.asList(90, 21, 19, 24, 24, 24, 24, 24, 24, 106);
        getRowConstraints().addAll(rowHeight.stream()
                .map(t -> new RowConstraints(t)).toList());

        for(int i=0; i<rowHeight.size(); i++) {
            getRowConstraints().get(i).setPercentHeight(rowHeight.get(i));
        }

        Label titleLabel = new Label("Game Server");
        titleLabel.setFont(new Font("Javanese Text", 24));
        add(titleLabel, 0, 0, 4, 1);

        roomTableView = new TableView<Room>();
        add(roomTableView, 0, 3, 1, 6);

        gameColumn = new TableColumn<Room, String>("Game");
        gameColumn.setCellValueFactory(
                new PropertyValueFactory<Room, String>("Game"));

        sizeColumn = new TableColumn<Room, String>("Size");
        sizeColumn.setCellValueFactory(
                new PropertyValueFactory<Room, String>("Size"));

        userColumn = new TableColumn<Room, Integer>("User");
        userColumn.setCellValueFactory(
                new PropertyValueFactory<Room, Integer>("User"));
        roomTableView.getColumns().addAll(gameColumn, sizeColumn, userColumn);

        roomTableView.setItems(data);

        createRoomLabel = new Label("Create Room");
        add(createRoomLabel, 3, 2, 1, 1);
        createRoomLabel.setTextAlignment(TextAlignment.CENTER);
        setHalignment(createRoomLabel, HPos.CENTER);

        choiceGameBox = new ChoiceBox<String>(FXCollections.observableArrayList("Tic tac toe", "Dots and boxes"));
        add(choiceGameBox,  3, 4, 1, 1);
        setFillWidth(choiceGameBox, true);
        setHalignment(choiceGameBox, HPos.CENTER);

        choiceSizeBox = new ChoiceBox<String>(FXCollections.observableArrayList("Small", "Medium",  "Big"));
        add(choiceSizeBox, 3, 6, 1, 1);
        setFillWidth(choiceSizeBox, true);
        setHgrow(choiceSizeBox, Priority.ALWAYS);
        setHalignment(choiceSizeBox, HPos.CENTER);

        createRoomButton = new Button("Create Room");
        add(createRoomButton, 3, 8, 1, 1);
        setHalignment(createRoomButton, HPos.CENTER);
    }
}
