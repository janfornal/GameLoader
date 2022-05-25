package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.client.GeneralView;
import GameLoader.client.PlayViewModel;

public class PaperSoccerViewModel implements PlayViewModel {
    public PaperSoccerViewModel(Client user, int id, PaperSoccer game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
    }

    @Override
    public PaperSoccer getGame() {
        return modelGame;
    }

    private final Client modelUser;
    private final PaperSoccer modelGame;
    private final int myPlayer;

    @Override
    public int playingAs() {
        return myPlayer;
    }

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public GeneralView createView() {
        return new PaperSoccerView(this);
    }
}
