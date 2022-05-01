package GameLoader.common;

import java.io.Serializable;

public record PlayerInfo(String name, int elo) implements Serializable {
    public PlayerInfo(String name) {
        this(name, 1000);
    }
}
