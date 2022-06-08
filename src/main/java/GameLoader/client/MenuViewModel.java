package GameLoader.client;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Messages.*;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.util.List;

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
            Button statisticsButton,
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
                ClientGUI.startStatisticsTab(row.getItem().p0().name());
            });
            rowMenu.getItems().addAll(editItem);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
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

        choiceGame.valueProperty().addListener((a, b, gameName) -> {
            List<String> poss = modelUser.gameTypeManager.possibleSettings(gameName);
            if (!poss.isEmpty())
                choiceSettings.setValue(poss.get(0));
        });
    }

    void addGetRoomHandler(Button button) {
        button.setOnMouseClicked(event -> {
            Message crm = new GetRoomListMessage();
            modelUser.sendMessage(crm);
        });
    }

    void addGetToStatisticsHandler(Button button) {
        button.setOnMouseClicked(event -> {
            ClientGUI.startStatisticsTab(modelUser.username);
        });
    }
}
