package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.PlayerInfo;
import com.sun.jdi.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DotsAndBoxes implements Game {

    private DotsAndBoxesBoard board;
    private String settings;
    private DotsAndBoxesCommand cmdCast;
    private boolean myTurn;

    public record DotsAndBoxesField(int row, int col, boolean marked) {

        public boolean isPoint(){
            return row%2 == 0 && col%2 == 0;
        }

        public boolean isEdge() {
            return (row + col)%2 == 0;
        }

        public boolean isSquare() {
            return row%2 == 1 && col%2 == 1;
        }

        public boolean isMarked() {
            if(!isEdge()) throw new IllegalArgumentException("Only edges can be marked");
            return marked;
        }
    }

    @Override
    public void makeMove(Command cmd) {
        if(cmd instanceof DotsAndBoxesCommand cmdCast) {
            if(!isMoveLegal(cmdCast)) return;
            // TODO
        }
        throw new ClassCastException("Wrong subclass of command");
    }

    @Override
    public boolean isMoveLegal(Command cmd) {
        if(cmd instanceof DotsAndBoxesCommand cmdCast) {
            return cmdCast.getField().isEdge() && !cmdCast.getField().isMarked();
        }
        throw new ClassCastException("Wrong subclass of command");
    }

    @Override
    public void start(String settings, int seed) {
        this.settings = settings;
        board = new DotsAndBoxesBoard(settings); // co gdy settings są złe (poza wyrzuceniem wyjątku)??
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return null;
    }

    @Override
    public PlayViewModel createViewModel(Client user, int id) {
        return new DotsAndBoxesViewModel(user, this);
    }

    @Override
    public String getName() {
        return "Dots and boxes";
    }

    @Override
    public Set<String> possibleSettings() {
        return Set.of("Size");
    }

    public class DotsAndBoxesBoard {
        int size;
        DotsAndBoxesField[][] fields;
        DotsAndBoxesBoard(String sizeType) {
            if(sizeType.equals("Small")) size = 3;
            else if(sizeType.equals("Medium")) size = 5;
            else if(sizeType.equals("Big")) size = 7;
            else {
                throw new IllegalArgumentException("Wrong settings were given");
            }
            fields = new DotsAndBoxesField[2*size+1][2*size+1];
            for(int i=0; i<2*size+1; i++) {
                for(int j=0; j<2*size+1; j++) fields[i][j] = new DotsAndBoxesField(i, j, false);
            }
        }
        public boolean isFieldInBoard(DotsAndBoxesField field) {
            return field.row >= 0 && field.row <= 2*size && field.col >= 0 && field.col <= 2*size;
        }

        public boolean isSurrounded(DotsAndBoxesField field) {
            int r = field.row;
            int c = field.col;
            if(!fields[r][c].isSquare()) throw new IllegalArgumentException("Only squares can be surrounded");
            return fields[r-1][c].isMarked() || fields[r+1][c].isMarked() || fields[r][c-1].isMarked() || fields[r][c+1].isMarked();
        }
    };

}
