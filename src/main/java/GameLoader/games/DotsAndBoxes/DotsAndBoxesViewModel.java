package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import static GameLoader.common.Serializables.Command;

public class DotsAndBoxesViewModel implements PlayViewModel {
    public DotsAndBoxesViewModel(Client user, int id, DotsAndBoxes game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
    }

    @Override
    public DotsAndBoxes getGame() {
        return modelGame;
    }

    private final Client modelUser;
    private final DotsAndBoxes modelGame;
    private final int myPlayer;

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public DotsAndBoxesView createView() {
        return new DotsAndBoxesView(this);
    }

    public void clickedOn(DotsAndBoxes.Coord c) {
        Command cmd = new DotsAndBoxesCommand(playingAs(), c);
        userCmd(cmd);
    }

    @Override
    public int playingAs() {
        return myPlayer;
    }
}
