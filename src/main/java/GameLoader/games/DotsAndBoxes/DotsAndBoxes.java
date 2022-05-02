package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.common.Command;
import GameLoader.common.Game;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DotsAndBoxes implements Game {
    private Coord sz, mostRecent;
    private boolean[][] T;

    private String settings;
    private int moveCount = 0, turn;
    private SimpleIntegerProperty moveCountProperty;

    private int edgesLeft = -1;
    private final int[] score = new int[2];

    public record Coord(int row, int col) {
        public boolean isPoint(){
            return row%2 == 0 && col%2 == 0; // this does not work for negative numbers
        }

        public boolean isEdge() {
            return (row + col)%2 == 0;
        }

        public boolean isSquare() {
            return row%2 == 1 && col%2 == 1;
        }
    }

    @Override
    public void makeMove(Command cmd) { // assumes that isMoveLegal(move) returns true
        DotsAndBoxesCommand dabCmd = (DotsAndBoxesCommand) cmd;

        int pl = dabCmd.getPlayer();
        Coord c = dabCmd.getCoord();

        T[c.row][c.col] = true;
        --edgesLeft;
        mostRecent = c;

        int added = 0;
        for (Coord n : listOfNeighbours(c))
            if (n.isSquare() && isFieldInBoard(n) && isSurrounded(n))
                ++added;

        score[pl] += added;
        if (added == 0)
            turn = 1 - turn;

        moveCount++;
        if (moveCountProperty != null)
            moveCountProperty.set(moveCount);
    }

    @Override
    public boolean isMoveLegal(Command cmd) {
        if (cmd instanceof DotsAndBoxesCommand dabCmd) {
            int pl = dabCmd.getPlayer();
            Coord c = dabCmd.getCoord();
            return settings != null && getState() == state.UNFINISHED
                    && turn == pl && c.isEdge() && isFieldInBoard(c) && !isMarked(c);
        }
        return false;
    }

    @Override
    public void start(String settings, int seed) {
        sz = settingsMap.get(settings);
        if (sz == null)
            throw new IllegalArgumentException("these settings are not permitted");
        this.settings = settings;
        turn = seed & 1;

        T = new boolean[sz.row][sz.col];
        edgesLeft = 2 * sz.row * sz.col + sz.row + sz.col;
    }

    public boolean isFieldInBoard(Coord field) {
        return field.row >= 0 && field.row <= 2*sz.row && field.col >= 0 && field.col <= 2*sz.col;
    }

    public boolean isMarked(Coord fieldInBoard) {
        return T[fieldInBoard.row][fieldInBoard.col];
    }

    public List<Coord> listOfNeighbours(Coord c) {
        return List.of(
                new Coord(c.row+1, c.col+1),
                new Coord(c.row+1, c.col-1),
                new Coord(c.row-1, c.col+1),
                new Coord(c.row-1, c.col-1)
        );
    }

    public boolean isSurrounded(Coord field) {
        for (Coord n : listOfNeighbours(field))
            if (isFieldInBoard(n) && !isMarked(n))
                return false;
        return true;
    }

    private static final Map<String, Coord> settingsMap = Map.of(
            "Small", new Coord(2, 3),
            "Medium", new Coord(4, 5),
            "Big", new Coord(6, 8)
    );

    @Override
    public String getName() {
        return "Dots and boxes";
    }

    @Override
    public Set<String> possibleSettings() {
        return settingsMap.keySet();
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        if (edgesLeft == 0)
            return state.UNFINISHED;
        if (score[0] == score[1])
            return state.DRAW;
        return score[0] > score[1] ? state.P0_WON : state.P1_WON;
    }

    @Override
    public DotsAndBoxesViewModel createViewModel(Client user, int id) {
        return new DotsAndBoxesViewModel(user, id, this);
    }

    public Coord getSize() {
        return sz;
    }

    public boolean getMarkedAt(Coord c) {
        return T[c.row][c.col];
    }

    public int getScore(int i) {
        return score[i];
    }

    public int getTurn() {
        return turn;
    }

    public Coord mostRecentMarking() {
        return mostRecent;
    }

    public ReadOnlyIntegerProperty getMoveCountProperty() {
        if (moveCountProperty == null)
            moveCountProperty = new SimpleIntegerProperty(moveCount);
        return moveCountProperty;
    }
}
