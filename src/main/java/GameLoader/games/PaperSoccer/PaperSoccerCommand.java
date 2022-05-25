package GameLoader.games.PaperSoccer;

import GameLoader.common.Command;
public class PaperSoccerCommand extends Command {
    private final int row, col;

    public PaperSoccerCommand(int player, int r, int c) {
        super(player);
        row = r;
        col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "PaperSoccerCommand{" +
                "row=" + row +
                ", col=" + col +
                ", player=" + getPlayer() +
                '}';
    }
}
