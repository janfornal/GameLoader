package GameLoader.client;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Messages.*;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RoomInfo rowData = row.getItem();
                    Message jr = new JoinRoomMessage(rowData);
                    modelUser.sendMessage(jr);
                }
            });
            return row;
        });
    }

    void addCreateRoomHandler(Button button) {
        button.setOnMouseClicked(event -> {
            String game = guiVisual.choiceGameBox().getValue();
            String settings = guiVisual.choiceSizeBox().getValue();

            if (game == null || settings == null) {
                Platform.runLater(
                        () -> new Alert(Alert.AlertType.ERROR, "Please select game and settings").showAndWait()
                );
                return;
            }

            Message crm = new CreateRoomMessage(game, settings);
            modelUser.sendMessage(crm);
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
            Message crm = new GetRoomListMessage();
            modelUser.sendMessage(crm);
        });
    }
}
