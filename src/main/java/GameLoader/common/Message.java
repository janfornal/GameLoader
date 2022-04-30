package GameLoader.common;

import java.io.Serializable;
import java.util.ArrayList;

public interface Message extends Serializable {
    interface Any extends Serializable {}
    interface CtoS extends Any {}
    interface StoC extends Any {}

    record Error(String cause) implements CtoS, StoC {}
    record Authorization(String name) implements CtoS {}

    record GetRoomList() implements CtoS {} // prośba do serwera o poniższe
    record RoomList(ArrayList<RoomInfo> rooms) implements StoC {} // przesyłamy listę obiektów typu RoomInfo które mówią o nierozpoczętych jeszcze grach

    record CreateRoom(String game, String settings) implements CtoS {} // przesyłamy klasę gry którą chcemy stworzyć
    record JoinRoom(RoomInfo room) implements CtoS {} // dołączamy do pokoju

    record StartGame(String game, String settings, PlayerInfo p0, PlayerInfo p1, int seed) implements StoC {} // gra ma się rozpocząć
    record Move(Command move) implements CtoS, StoC {} // wykonany ruch

    record InterruptedGame(String cause) implements Any {} // to jest na razie nieistotne
    record LeaveRoom(String player) implements Any {} // to jest na razie nieistotne
    record Resign(String player) implements Any {} // to jest na razie nieistotne

    record GameList() implements Any {} // to jest na razie nieistotne
    record GetGameList() implements Any {} // to jest na razie nieistotne
}
