package GameLoader.common;

import java.io.Serializable;

public interface Message extends Serializable {
    interface Any extends Serializable{}
    record Error(String cause) implements Any{}
    record Authorization(String name) implements Any{}
    record CreateRoom(String game) implements Any{}
    record GameList() implements Any{}
    record GetGamesList(String player) implements Any{} //player - ktory gracz sie domaga tej listy
    record GetRoomList(String player) implements Any{}
    record InterruptedGame(String cause) implements Any{}
    record JoinRoom(String player) implements Any{} //-kto dolacza
    record LeaveRoom(String player) implements Any{} //-kto wychodzi
    record Move(String move) implements Any{}
    record Resign(String player) implements Any{}
    record RoomList() implements Any{}
    record StartGame() implements Any{}
}
