package GameLoader.games.PaperSoccer;

import static GameLoader.common.Serializables.Command;

public class PaperSoccerCommand extends Command {
    private final int dir;

    public PaperSoccerCommand(int player, int d) {
        super(player);
        dir = d;
    }

    public int getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return "PaperSoccerCommand{" +
                "dir=" + dir +
                ", player=" + getPlayer() +
                '}';
    }
}
