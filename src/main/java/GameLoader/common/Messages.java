package GameLoader.common;

import java.io.Serializable;
import java.util.ArrayList;
import static GameLoader.common.Serializables.*;

public interface Messages {
    interface AnyMessage extends Serializable {}

    interface ClientToServerMessage extends AnyMessage {}
    interface ServerToClientMessage extends AnyMessage {}

    record PingMessage(String p) implements ClientToServerMessage, ServerToClientMessage {}
    record PongMessage(String p) implements ClientToServerMessage, ServerToClientMessage {}

    record ErrorMessage(String cause) implements ClientToServerMessage, ServerToClientMessage {}

    record AuthorizationAttemptMessage(String name, String password) implements ClientToServerMessage {}
    record RegistrationAttemptMessage(String name, String password) implements ClientToServerMessage {}

    record SuccessfulAuthorizationMessage() implements ServerToClientMessage {}
    record UnsuccessfulAuthorizationMessage(String cause) implements ServerToClientMessage {}

    record GetRoomListMessage() implements ClientToServerMessage {} // prośba do serwera o poniższe
    record RoomListMessage(ArrayList<RoomInfo> rooms) implements ServerToClientMessage {} // przesyłamy listę obiektów typu RoomInfo które mówią o nierozpoczętych jeszcze grach

    record CreateRoomMessage(String game, String settings) implements ClientToServerMessage {} // przesyłamy klasę gry którą chcemy stworzyć
    record JoinRoomMessage(RoomInfo room) implements ClientToServerMessage {} // dołączamy do pokoju

    record StartGameMessage(String game, String settings, int seed, PlayerInfo p0, PlayerInfo p1) implements ServerToClientMessage {} // gra ma się rozpocząć
    record MoveMessage(Command move) implements ClientToServerMessage, ServerToClientMessage {} // wykonany ruch

    record InterruptedGameMessage(String cause) implements AnyMessage {} // to jest na razie nieistotne
    record EndConnectionMessage() implements AnyMessage {}

    record GameListMessage() implements AnyMessage {} // to jest na razie nieistotne
    record GetGameListMessage() implements AnyMessage {} // to jest na razie nieistotne
    record ChatMessage(String text) implements AnyMessage {}
}
