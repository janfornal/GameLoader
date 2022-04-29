package GameLoader.common;

import java.io.Serializable;

public interface Game {
    interface Command extends Serializable {

    }

    interface GameInfo extends Serializable {
        String getInfo();
        String getName();
        PlayerInfo getPlayer();
    }
}
