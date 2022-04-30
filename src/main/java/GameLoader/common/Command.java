package GameLoader.common;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private final int player;

    protected Command(int pl) {
        if (pl != 0 && pl != 1)
            throw new IllegalArgumentException("player should be equal to 0 or 1");
        player = pl;
    }

    public final int getPlayer() {
        return player;
    }
}
