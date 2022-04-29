package GameLoader.client;

import GameLoader.common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public guiElements getElements() {
        return guiVisual;
    }

    record guiElements(
            ChoiceBox<String> choiceGameBox,
            ChoiceBox<String> choiceSizeBox,
            Label createRoomLabel,
            TableView<Game.GameInfo> roomTableView,
            Button createRoomButton,
            TableColumn<Game.GameInfo, String> gameColumn,
            TableColumn<Game.GameInfo, String> sizeColumn,
            TableColumn<Game.GameInfo, String> userColumn,
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

    void addGetToRoomHandler(TableView<Game.GameInfo> table) {
        table.setRowFactory(tv -> {
            TableRow<Game.GameInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Game.GameInfo rowData = row.getItem();
                    modelUser.setChosenGame(rowData);
                    Message.Any jr = new Message.JoinRoom(rowData.getPlayer());
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
                Game.GameInfo dataInfo = new Game.GameInfo() {
                    @Override
                    public String getInfo() {
                        return guiVisual.choiceSizeBox().getValue();
                    }

                    @Override
                    public String getName() {
                        return guiVisual.choiceGameBox().getValue();
                    }

                    @Override
                    public PlayerInfo getPlayer() {
                        return modelUser.username;
                    }
                };
                System.out.println(dataInfo.getClass());
                Message.Any crm = new Message.CreateRoom(dataInfo);
                modelUser.setChosenGame(dataInfo);
                modelUser.sendMessage(crm);
            }
        });
    }
}
