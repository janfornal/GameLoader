package GameLoader.client;

import GameLoader.games.Command;
import GameLoader.games.Game;
import GameLoader.common.PlayerInfo;

public class DotsAndBoxes implements Game {

    private DotsAndBoxesBoard board;

    public record DotsAndBoxesField(int row, int col) {

        public boolean isPoint(){
            return row%2 == 0 && col%2 == 0;
        }

        public boolean isEdge() {
            return (row + col)%2 == 0;
        }

        public boolean isSquare() {
            return row%2 == 1 && col%2 == 1;
        }

    }

    @Override
    public DotsAndBoxes createNewGame() {
        return null;
    }

    @Override
    public void makeMove(Command cmd) {

    }

    @Override
    public boolean isLegal(Command cmd) {
        return false;
    }

    @Override
    public PlayerInfo[] players() {
        return new PlayerInfo[0];
    }

    @Override
    public GameInfo getGameInfo() {
        return null;
    }

    public class DotsAndBoxesBoard {
        int size;
        DotsAndBoxesField[][] fields;
        DotsAndBoxesBoard(String sizeType) {
            if(sizeType.equals("small")) size = 3;
            if(sizeType.equals("medium")) size = 5;
            if(sizeType.equals("big")) size = 7;
            fields = new DotsAndBoxesField[2*size+1][2*size+1];
        }
        public boolean isFieldInBoard(DotsAndBoxesField field) {
            return field.row >= 0 && field.row <= 2*size && field.col >= 0 && field.col <= 2*size;
        }
    };

}
