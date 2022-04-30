package GameLoader.common;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private final int player;

    protected Command(int pl) {
        player = pl;
    }

    public final int getPlayer() {
        return player;
    }
}
