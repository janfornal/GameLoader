package GameLoader.common;

import java.io.Serializable;

public record PlayerInfo(String name, int elo) implements Serializable {
}
