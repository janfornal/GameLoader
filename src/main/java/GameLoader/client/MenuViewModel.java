package GameLoader.client;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Messages.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Collections;

public class MenuViewModel implements ViewModel {

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public void setElements(GuiElements fooElements) {
        guiVisual = (guiElements) fooElements;
    }

    @Override
    public GeneralView createView() {
        return new MenuView(this);
    }

    public guiElements getElements() {
        return guiVisual;
    }

    record guiElements(
            ChoiceBox<String> choiceGameBox,
            ChoiceBox<String> choiceSizeBox,
            //Label createRoomLabel,
            TableView<RoomInfo> roomTableView,
            Button createRoomButton,
            Button getRoomList,
            TableColumn<RoomInfo, String> gameColumn,
            TableColumn<RoomInfo, String> sizeColumn,
            TableColumn<RoomInfo, String> userColumn,
            TableColumn<RoomInfo, Integer> eloColumn,
            Label titleLabel
    ) implements GuiElements {
    }

    int prefWindowWidth = 800;
    int prefWindowHeight = 600;
    private guiElements guiVisual;
    private final Client modelUser;

    public MenuViewModel(Client user) {
        modelUser = user;
    }

    void addGetToRoomHandler(TableView<RoomInfo> table) {
        table.setRowFactory(tv -> {
            TableRow<RoomInfo> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Show User's statistics");
            editItem.setOnAction(event -> {
                ClientGUI.startStatisticsTab();
            });
            rowMenu.getItems().addAll(editItem);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RoomInfo rowData = row.getItem();
                    AnyMessage jr = new JoinRoomMessage(rowData);
                    modelUser.sendMessage(jr);
                }
            });
            return row;
        });
    }

    void addCreateRoomHandler(Button button) {
        button.setOnMouseClicked(event -> {
            if (guiVisual.choiceSizeBox.getValue() == null || guiVisual.choiceSizeBox.getValue().equals("Please select size")) {
                guiVisual.choiceSizeBox.setValue("Please select size");
                if (guiVisual.choiceGameBox.getValue() == null || guiVisual.choiceGameBox.getValue().equals("Please select game")) {
                    guiVisual.choiceGameBox.setValue("Please select game");
                }
            } else if (guiVisual.choiceGameBox.getValue() == null || guiVisual.choiceGameBox.getValue().equals("Please select game")) {
                guiVisual.choiceGameBox.setValue("Please select game");
            } else {
                String game = guiVisual.choiceGameBox().getValue();
                String settings = guiVisual.choiceSizeBox().getValue();

                AnyMessage crm = new CreateRoomMessage(game, settings);
                modelUser.sendMessage(crm);
            }
        });
    }

    void addChoiceSettingsHandler(ChoiceBox<String> choiceGame, ChoiceBox<String> choiceSettings) {
        choiceSettings.itemsProperty().bind(Bindings.createObjectBinding(
                () -> FXCollections.observableArrayList(
                        modelUser.gameTypeManager.possibleSettings(choiceGame.getValue())
                ),
                choiceGame.valueProperty()
        ));
    }

    void addGetRoomHandler(Button button) {
        button.setOnMouseClicked(event -> {
            AnyMessage crm = new GetRoomListMessage();
            modelUser.sendMessage(crm);
        });
    }
}
