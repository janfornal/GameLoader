package GameLoader.common;

public class ResignationCommand extends Command {
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
