package GameLoader.common;

import java.io.Serializable;

public record RoomInfo(String game, String settings, PlayerInfo p0) implements Serializable {
}
