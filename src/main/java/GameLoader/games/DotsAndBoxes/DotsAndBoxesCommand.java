package GameLoader.games.DotsAndBoxes;

import GameLoader.common.Command;

public class DotsAndBoxesCommand extends Command {
    private final DotsAndBoxes.DotsAndBoxesField field;

    public DotsAndBoxesCommand(int player, DotsAndBoxes.DotsAndBoxesField field) {
        super(player);
        this.field = field;
        int r = field.row();
        int c = field.col();
        if (r < 0 || c < 0 || (r + c)%2 == 0)
            throw new IllegalArgumentException("illegal board coordinate: (" + r + ", " + c + ")");
    }

    public DotsAndBoxes.DotsAndBoxesField getField() {
        return field;
    }

}
