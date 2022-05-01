package GameLoader.games.DotsAndBoxes;

import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.PlayerInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void makeMove(Command cmd) {

    }

    @Override
    public boolean isMoveLegal(Command cmd) {
        return false;
    }

    @Override
    public void start(String settings, int seed) {

    }

    @Override
    public String getSettings() {
        return null;
    }

    @Override
    public state getState() {
        return null;
    }

    @Override
    public String getName() {
        return "DotsAndBoxes";
    }

    @Override
    public Set<String> possibleSettings() {
        return new HashSet<String>(List.of(new String[]{"Size"}));
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