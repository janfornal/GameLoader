package GameLoader.games;

import java.io.Serializable;

public abstract class Command implements Serializable {
    public final int player;

    protected Command(int pl) {
        player = pl;
    }
}
