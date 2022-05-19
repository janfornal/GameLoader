package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToe;
import GameLoader.games.TicTacToe.TicTacToe;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.Arrays;
import java.util.List;

public class MenuView extends GridPane implements GeneralView {

    public MenuView(MenuViewModel nvm) {

        setPrefSize(nvm.prefWindowWidth, nvm.prefWindowHeight);

        setPadding(new Insets(10, 20, 10, 20));

        List<Integer> columnWidth = Arrays.asList(270, 100, 210);
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
            new ChoiceBox<String>(FXCollections.observableArrayList(new SimpleTicTacToe().getName(), new DotsAndBoxes().getName(), new TicTacToe().getName())),
            new ChoiceBox<String>(),
            new Label("Create Room"),
            new TableView<RoomInfo>(),
            new Button("Create Room"),
            new Button("Get Room List"),
            new TableColumn<RoomInfo, String>("Game"),
            new TableColumn<RoomInfo, String>("Size"),
            new TableColumn<RoomInfo, String>("User"),
            new Label("Game Server")
        ));

        nvm.getElements().titleLabel().setFont(new Font("Javanese Text", 24));
        add(nvm.getElements().titleLabel(), 0, 0, 4, 1);

        add(nvm.getElements().roomTableView(), 0, 3, 1, 6);

        nvm.getElements().roomTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nvm.getElements().gameColumn().setCellValueFactory(
                g -> new ReadOnlyObjectWrapper<String>(g.getValue().game())
        );

        nvm.getElements().sizeColumn().setCellValueFactory(
                g -> new ReadOnlyObjectWrapper<String>(g.getValue().settings())
        );

        nvm.getElements().userColumn().setCellValueFactory(
                g -> new ReadOnlyObjectWrapper<String>(g.getValue().p0().name())
        );

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
        nvm.addChoiceSettingsHandler(nvm.getElements().choiceSizeBox());

        add(nvm.getElements().createRoomButton(), 2, 8, 1, 1);
        setHalignment(nvm.getElements().createRoomButton(), HPos.LEFT);
        nvm.addCreateRoomHandler(nvm.getElements().createRoomButton());

        add(nvm.getElements().getRoomList(), 2, 8, 1, 1);
        setHalignment(nvm.getElements().getRoomList(), HPos.RIGHT);
        nvm.addGetRoomHandler(nvm.getElements().getRoomList());
    }
}
