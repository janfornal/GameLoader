package GameLoader.common;

import java.io.Serializable;
import java.util.ArrayList;

public interface Message extends Serializable {
    interface Any extends Serializable {}
    record Error(String cause) implements Any {}
    record Authorization(String name) implements Any {}
    record CreateRoom(Game.GameInfo game) implements Any {} // przesyłamy klasę gry którą chcemy stworzyć
    record GameList() implements Any {} // to jest na razie nieistotne
    record GetGamesList(String player) implements Any {} // to jest na razie nieistotne
    record RoomList(ArrayList<Game.GameInfo> rooms) implements Any {} // przesyłamy listę obiektów typu GameInfo które mówią o nierozpoczętych jeszcze grach
    record GetRoomList() implements Any {} // prośba do serwera o powyższe
    record InterruptedGame(String cause) implements Any {} // to jest na razie nieistotne
    record JoinRoom(Game.GameInfo game) implements Any {} // kto dolacza do pokoju który stworzyliśmy
    record LeaveRoom(PlayerInfo player) implements Any {} // to jest na razie nieistotne
    record Move(Game.Command move) implements Any {} // wykonany ruch
    record Resign(PlayerInfo player) implements Any {} // to jest na razie nieistotne
    record StartGame(PlayerInfo p1, PlayerInfo p2) implements Any {} // gra ma się rozpocząć
}
