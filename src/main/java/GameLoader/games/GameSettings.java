package GameLoader.games;

import java.io.Serializable;

public record GameSettings(String name, String settings) implements Serializable {
}
