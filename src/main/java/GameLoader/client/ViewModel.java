package GameLoader.client;

import GameLoader.games.DotsAndBoxes.*;
import GameLoader.games.SimpleTicTacToe.*;

import java.util.HashMap;

public interface ViewModel {
    Client getModelUser();
    void setElements(GuiElements fooElements);
    GeneralView createView();
}
