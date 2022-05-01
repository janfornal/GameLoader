package GameLoader.client;

import GameLoader.common.Game;
import GameLoader.common.PlayerInfo;
import GameLoader.common.RoomInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.List;

public class MenuView extends GridPane implements GeneralView {

    public MenuView(MenuViewModel nvm) {

        setPrefSize(nvm.prefWindowWidth, nvm.prefWindowHeight);

        setPadding(new Insets(10, 20, 10, 20));

        List<Integer> columnWidth = Arrays.asList(270, 160, 150);
        this.getColumnConstraints().addAll(columnWidth.stream()
                .map(t -> new ColumnConstraints()).toList());

        for(int i=0; i<columnWidth.size(); i++) {
            this.getColumnConstraints().get(i).setPercentWidth(columnWidth.get(i));
        }

        List <Integer> rowHeight = Arrays.asList(90, 21, 19, 24, 24, 24, 24, 24, 24, 106);
        this.getRowConstraints().addAll(rowHeight.stream()
                .map(t -> new RowConstraints()).toList());

        for(int i=0; i<rowHeight.size(); i++) {
            this.getRowConstraints().get(i).setPercentHeight(rowHeight.get(i));
        }

        nvm.setElements(new MenuViewModel.guiElements(
            new ChoiceBox<String>(FXCollections.observableArrayList("Tic tac toe", "Dots and boxes")),
            new ChoiceBox<String>(FXCollections.observableArrayList("Small", "Medium",  "Big")),
            new Label("Create Room"),
            new TableView<RoomInfo>(),
            new Button("Create Room"),
            new TableColumn<RoomInfo, String>("Game"),
            new TableColumn<RoomInfo, String>("Size"),
            new TableColumn<RoomInfo, PlayerInfo>("User"),
            new Label("Game Server")
        ));

        nvm.getElements().titleLabel().setFont(new Font("Javanese Text", 24));
        add(nvm.getElements().titleLabel(), 0, 0, 4, 1);

        final ObservableList<RoomInfo> data =
                FXCollections.observableArrayList(
                        new RoomInfo("Dots and boxes", "Small", new PlayerInfo("2819"))
                );

        add(nvm.getElements().roomTableView(), 0, 3, 1, 6);
        nvm.getElements().roomTableView().setItems(data);

        nvm.getElements().gameColumn().setCellValueFactory(
                new PropertyValueFactory<RoomInfo, String>("Game"));

        nvm.getElements().sizeColumn().setCellValueFactory(
                new PropertyValueFactory<RoomInfo, String>("Size"));

        nvm.getElements().userColumn().setCellValueFactory(
                new PropertyValueFactory<RoomInfo, PlayerInfo>("User"));

        nvm.getElements().roomTableView().getColumns().addAll(
                nvm.getElements().gameColumn(),
                nvm.getElements().sizeColumn(),
                nvm.getElements().userColumn());
        nvm.addGetToRoomHandler(nvm.getElements().roomTableView());

        add(nvm.getElements().createRoomLabel(), 2, 2, 1, 1);
        nvm.getElements().createRoomLabel().setTextAlignment(TextAlignment.CENTER);
        setHalignment(nvm.getElements().createRoomLabel(), HPos.CENTER);

        add(nvm.getElements().choiceGameBox(),  2, 4, 1, 1);
        setHalignment(nvm.getElements().choiceGameBox(), HPos.CENTER);

        add(nvm.getElements().choiceSizeBox(), 2, 6, 1, 1);
        setFillWidth(nvm.getElements().choiceSizeBox(), true);
        setHgrow(nvm.getElements().choiceSizeBox(), Priority.ALWAYS);
        setHalignment(nvm.getElements().choiceSizeBox(), HPos.CENTER);

        add(nvm.getElements().createRoomButton(), 2, 8, 1, 1);
        setHalignment(nvm.getElements().createRoomButton(), HPos.CENTER);
        nvm.addCreateRoomHandler(nvm.getElements().createRoomButton());

    }
}
