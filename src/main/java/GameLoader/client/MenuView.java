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

        nvm.guiVisual = new MenuViewModel.guiElements(
            new ChoiceBox<String>(FXCollections.observableArrayList("Tic tac toe", "Dots and boxes")),
            new ChoiceBox<String>(FXCollections.observableArrayList("Small", "Medium",  "Big")),
            new Label("Create Room"),
            new TableView<MenuViewModel.Room>(),
            new Button("Create Room"),
            new TableColumn<MenuViewModel.Room, String>("Game"),
            new TableColumn<MenuViewModel.Room, String>("Size"),
            new TableColumn<MenuViewModel.Room, Integer>("User"),
            new Label("Game Server")
        );

        nvm.guiVisual.titleLabel().setFont(new Font("Javanese Text", 24));
        add(nvm.guiVisual.titleLabel(), 0, 0, 4, 1);

        add(nvm.guiVisual.roomTableView(), 0, 3, 1, 6);

        nvm.guiVisual.gameColumn().setCellValueFactory(
                new PropertyValueFactory<MenuViewModel.Room, String>("Game"));

        nvm.guiVisual.sizeColumn().setCellValueFactory(
                new PropertyValueFactory<MenuViewModel.Room, String>("Size"));

        nvm.guiVisual.userColumn().setCellValueFactory(
                new PropertyValueFactory<MenuViewModel.Room, Integer>("User"));

        nvm.guiVisual.roomTableView().getColumns().addAll(
                nvm.guiVisual.gameColumn(),
                nvm.guiVisual.sizeColumn(),
                nvm.guiVisual.userColumn());
        nvm.addGetToRoomHandler(nvm.guiVisual.roomTableView());

        add(nvm.guiVisual.createRoomLabel(), 2, 2, 1, 1);
        nvm.guiVisual.createRoomLabel().setTextAlignment(TextAlignment.CENTER);
        setHalignment(nvm.guiVisual.createRoomLabel(), HPos.CENTER);

        add(nvm.guiVisual.choiceGameBox(),  2, 4, 1, 1);
        setHalignment(nvm.guiVisual.choiceGameBox(), HPos.CENTER);

        add(nvm.guiVisual.choiceSizeBox(), 2, 6, 1, 1);
        setFillWidth(nvm.guiVisual.choiceSizeBox(), true);
        setHgrow(nvm.guiVisual.choiceSizeBox(), Priority.ALWAYS);
        setHalignment(nvm.guiVisual.choiceSizeBox(), HPos.CENTER);

        add(nvm.guiVisual.createRoomButton(), 2, 8, 1, 1);
        setHalignment(nvm.guiVisual.createRoomButton(), HPos.CENTER);
        nvm.addCreateRoomHandler(nvm.guiVisual.createRoomButton());

    }
}
