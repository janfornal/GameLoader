package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import javafx.beans.Observable;

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
    public PaperSoccerView createView() {
        return new PaperSoccerView(this);
    }

    @Override
    public Observable getObservable() {
        return getGame().getMoveCountProperty();
    }
}
