package GameLoader.games.DotsAndBoxes;

import GameLoader.client.Client;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.ResignationCommand;
import javafx.beans.property.*;

import java.io.Serializable;
import java.util.*;

public class DotsAndBoxes implements Game {
    private static final List<String> settingsList;
    private static final Map<String, Coord> settingsMap;
    static {
        Map<String, Coord> mp = new LinkedHashMap<>();
        mp.put("Small", new Coord(2, 3));
        mp.put("Medium", new Coord(4, 5));
        mp.put("Large", new Coord(6, 8));
        settingsMap = Collections.unmodifiableMap(mp);
        settingsList = List.copyOf(mp.keySet());
    }

    private Coord sz, mostRecent;
    private int[][] T;
    // 0 = unmarked, 1 = marked for edges
    // 0 = empty, 1 = p0, 2 = p1 for squares
    // 0 for corners

    private String settings;
    private state currState = state.UNFINISHED;
    private int moveCount = 0, turn;
    private SimpleIntegerProperty moveCountProperty;

    private int edgesLeft = -1;
    private final int[] score = new int[2];

    public record Coord(int row, int col) implements Serializable {
        public boolean isPoint(){
            return row%2 == 0 && col%2 == 0; // this does not work for negative numbers
        }

        public boolean isEdge() {
            return (row + col)%2 == 1;
        }

        public boolean isSquare() {
            return row%2 == 1 && col%2 == 1;
        }
    }

    @Override
    public void makeMove(Command cmd) { // assumes that isMoveLegal(move) returns true
        if (cmd instanceof ResignationCommand res)
            currState = res.getPlayer() == 0 ? state.P1_WON : state.P0_WON;
        if (cmd instanceof DotsAndBoxesCommand dabCmd)
        {
            int pl = dabCmd.getPlayer();
            Coord c = dabCmd.getCoord();

            T[c.row][c.col] = 1;
            --edgesLeft;
            mostRecent = c;

            int added = 0;
            for (Coord n : listOfNeighbours(c))
                if (n.isSquare() && isFieldInBoard(n) && isSurrounded(n)) {
                    ++added;
                    T[n.row][n.col] = pl + 1;
                }

            score[pl] += added;
            if (added == 0)
                turn = 1 - turn;
            currState = calcState();
        }

        moveCount++;
        if (moveCountProperty != null)
            moveCountProperty.set(moveCount);
    }

    @Override
    public boolean isMoveLegal(Command cmd) {
        if (cmd instanceof ResignationCommand)
            return getState() == state.UNFINISHED;
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

        T = new int[sz.row*2+1][sz.col*2+1];
        edgesLeft = 2 * sz.row * sz.col + sz.row + sz.col;
    }

    public boolean isFieldInBoard(Coord field) {
        return field.row >= 0 && field.row <= 2*sz.row && field.col >= 0 && field.col <= 2*sz.col;
    }

    public boolean isMarked(Coord edgeInBoard) {
        return T[edgeInBoard.row][edgeInBoard.col] == 1;
    }

    public int getOwner(Coord squareInBoard) {
        return T[squareInBoard.row][squareInBoard.col] - 1;
    }

    public List<Coord> listOfNeighbours(Coord c) {
        return List.of(
                new Coord(c.row+1, c.col),
                new Coord(c.row-1, c.col),
                new Coord(c.row, c.col+1),
                new Coord(c.row, c.col-1)
        );
    }

    public boolean isSurrounded(Coord field) {
        for (Coord n : listOfNeighbours(field))
            if (isFieldInBoard(n) && !isMarked(n))
                return false;
        return true;

    }

    public boolean isAlone(Coord field) {
        for (Coord n : listOfNeighbours(field))
            if (isFieldInBoard(n) && isMarked(n))
                return false;
        return true;
    }

    @Override
    public String getName() {
        return "Dots and boxes";
    }

    @Override
    public List<String> possibleSettings() {
        return settingsList;
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return currState;
    }

    private state calcState() {
        if (edgesLeft != 0)
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

    @Override
    public String toString() {
        return "DotsAndBoxes{" +
                "sz=" + sz +
                ", mostRecent=" + mostRecent +
                ", T=" + Arrays.deepToString(T) +
                ", settings='" + settings + '\'' +
                ", moveCount=" + moveCount +
                ", turn=" + turn +
                ", edgesLeft=" + edgesLeft +
                ", score=" + Arrays.toString(score) +
                '}';
    }
}
