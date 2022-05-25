package GameLoader.client;

import GameLoader.common.*;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

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
            Label createRoomLabel,
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

    int prefWindowWidth = 600;
    int prefWindowHeight = 400;
    private guiElements guiVisual;
    private final Client modelUser;

    public MenuViewModel(Client user) {
        modelUser = user;
    }

    void addGetToRoomHandler(TableView<RoomInfo> table) {
        table.setRowFactory(tv -> {
            TableRow<RoomInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RoomInfo rowData = row.getItem();
                    Message.Any jr = new Message.JoinRoom(rowData);
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

                Message.Any crm = new Message.CreateRoom(game, settings);
                modelUser.sendMessage(crm);
            }
        });
    }

    void addChoiceSettingsHandler(ChoiceBox<String> choiceBox) {
        choiceBox.setOnMouseClicked(event -> {
            if (guiVisual.choiceGameBox.getValue() == null || guiVisual.choiceGameBox.getValue().equals("Please select game"))
                guiVisual.choiceGameBox.setValue("Please select game");
            else
                choiceBox.setItems(FXCollections.observableList(modelUser.getGameSettings(guiVisual.choiceGameBox.getValue()).stream().toList()));
        });
    }

    void addGetRoomHandler(Button button) {
        button.setOnMouseClicked(event -> {
            Message.Any crm = new Message.GetRoomList();
            modelUser.sendMessage(crm);
        });
    }
}
