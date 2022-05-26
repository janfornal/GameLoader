package GameLoader.games.DotsAndBoxes;

import static GameLoader.common.Serializables.Command;

public class DotsAndBoxesCommand extends Command {
    private final DotsAndBoxes.Coord crd;

    public DotsAndBoxesCommand(int player, DotsAndBoxes.Coord coord) {
        super(player);
        crd = coord;
        int r = coord.row();
        int c = coord.col();
        if (r < 0 || c < 0 || (r + c)%2 == 0)
            throw new IllegalArgumentException("illegal board coordinate: (" + r + ", " + c + ")");
    }

    public DotsAndBoxes.Coord getCoord() {
        return crd;
    }

    @Override
    public String toString() {
        return "DotsAndBoxesCommand{" +
                "coord=" + crd +
                ", player=" + getPlayer() +
                '}';
    }
}
