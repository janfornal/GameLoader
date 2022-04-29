package GameLoader.games;

import GameLoader.common.PlayerInfo;
import GameLoader.games.GameSettings;

import java.io.Serializable;

public record RoomInfo(GameSettings settings, PlayerInfo p0) implements Serializable {
}
