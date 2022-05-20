package GameLoader.common;

import java.io.Serializable;
import java.util.ArrayList;

public interface Message {
    interface Any extends Serializable {}
    interface CtoS extends Any {}
    interface StoC extends Any {}

    record Ping(String p) implements CtoS, StoC {}
    record Pong(String p) implements CtoS, StoC {}

    record Error(String cause) implements CtoS, StoC {}

    record AuthorizationAttempt(String name, String password) implements CtoS {}
    record RegistrationAttempt(String name, String password) implements CtoS {}

    record SuccessfulAuthorization() implements StoC {}
    record UnsuccessfulAuthorization(String cause) implements StoC {}

    record GetRoomList() implements CtoS {} // prośba do serwera o poniższe
    record RoomList(ArrayList<RoomInfo> rooms) implements StoC {} // przesyłamy listę obiektów typu RoomInfo które mówią o nierozpoczętych jeszcze grach

    record CreateRoom(String game, String settings) implements CtoS {} // przesyłamy klasę gry którą chcemy stworzyć
    record JoinRoom(RoomInfo room) implements CtoS {} // dołączamy do pokoju

    record StartGame(String game, String settings, int seed, PlayerInfo p0, PlayerInfo p1) implements StoC {} // gra ma się rozpocząć
    record Move(Command move) implements CtoS, StoC {} // wykonany ruch

    record InterruptedGame(String cause) implements Any {} // to jest na razie nieistotne
    record EndConnection() implements Any {}

    record GameList() implements Any {} // to jest na razie nieistotne
    record GetGameList() implements Any {} // to jest na razie nieistotne
    record ChatMessage(String text) implements Any {}
}
