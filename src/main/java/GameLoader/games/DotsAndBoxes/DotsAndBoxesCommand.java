package GameLoader.games.DotsAndBoxes;

import GameLoader.common.Command;

public class DotsAndBoxesCommand extends Command {
    private final int row, col;

    public DotsAndBoxesCommand(int player, int r, int c) {
        super(player);
        if (r < 0 || c < 0)
            throw new IllegalArgumentException("illegal board coordinate: (" + r + ", " + c + ")");
        row = r;
        col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
