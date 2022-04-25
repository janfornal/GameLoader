package GameLoader.client;

import GameLoader.common.Connection;
import GameLoader.common.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

public class MenuViewModel implements ViewModel {

    public record Room (
        String game,
        String size,
        String user
    ){ }

    record guiElements(
        ChoiceBox<String> choiceGameBox,
        ChoiceBox<String> choiceSizeBox,
        Label createRoomLabel,
        TableView<Room> roomTableView,
        Button createRoomButton,
        TableColumn<Room, String> gameColumn,
        TableColumn<Room, String> sizeColumn,
        TableColumn<Room, Integer> userColumn,
        Label titleLabel
    ) { }

    int prefWindowWidth = 600;
    int prefWindowHeight = 400;
    guiElements guiVisual;
    private Connection c;

    public MenuViewModel(Connection c) {
        this.c = c;
    }

//    private final ObservableList<Room> data =
//            FXCollections.observableArrayList(
//                    new Room("Dots and boxes", "Small", 2819),
//                    new Room("Tic tac toe", "Big", 1782),
//                    new Room("Tic tac toe", "Medium", 2144),
//                    new Room("Dots and boxes", "Big", 839),
//                    new Room("Tic tac toe", "Big", 1012)
//            );

    void addGetToRoomHandler(TableView<Room> table) {
        table.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Room rowData = row.getItem();
                    Message.Any jr = new Message.JoinRoom(rowData.user());
                    record JoinRoom(String player) implements Message.Any {} //-kto dolacza
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
            }
            else if (guiVisual.choiceGameBox.getValue() == null || guiVisual.choiceGameBox.getValue().equals("Please select game")) {
                guiVisual.choiceGameBox.setValue("Please select game");
            }
            else {
                Message.Any crm = new Message.CreateRoom(
                    guiVisual.choiceGameBox.getValue() + "\n" + guiVisual.choiceSizeBox.getValue()
                );
                c.sendMessage(crm);
            }
        });
    }
}
