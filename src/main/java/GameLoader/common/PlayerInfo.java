package GameLoader.common;

import java.io.Serializable;

public record PlayerInfo(String name, int id, int elo) implements Serializable {
    public PlayerInfo(String name) { // TODO delete this
        this(name, 0, 1000);
    }
}
