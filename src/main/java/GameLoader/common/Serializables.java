package GameLoader.common;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

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

    abstract class Query implements Serializable {
        private final String player;
        private final String gameName;

        protected Query(String player, String gameName) {
            this.player = player;
            this.gameName = gameName;
        }

        public final String getPlayer() {
            return player;
        }

        public final String getGame() {
            return gameName;
        }
    }

    class StatisticsQuery extends Query {
        public StatisticsQuery(String player, String gameName) {
            super(player, gameName);
        }
    }

    class EloQuery extends Query {
        public EloQuery(String player, String gameName) {
            super(player, gameName);
        }
    }

    class GamesQuery extends Query {
        public GamesQuery(String player, String gameName) {
            super(player, gameName);
        }
    }

    interface DatabaseAnswer extends Serializable {}

    record StatisticsAnswer(ArrayList<Pair<String, Integer>> eloList) implements DatabaseAnswer {}
    record EloAnswer(String game, int value) implements DatabaseAnswer {}
    record GamesAnswer(String game, int won, int draw, int lost) implements DatabaseAnswer {}
}
