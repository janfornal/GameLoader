package GameLoader.common;

import java.io.Serializable;

public interface Serializables {
    record PlayerInfo(String name, int elo) implements Serializable {}
    record RoomInfo(String game, String settings, PlayerInfo p0) implements Serializable {}

    abstract class Command implements Serializable {
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

    class ResignationCommand extends Command {
        public ResignationCommand(int player) {
            super(player);
        }

        @Override
        public String toString() {
            return "ResignationCommand{" +
                    "player=" + getPlayer() +
                    "}";
        }
    }
}
