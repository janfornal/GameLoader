package GameLoader.games.TicTacToe;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.games.DotsAndBoxes.DotsAndBoxes;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToeCommand;
import GameLoader.games.SimpleTicTacToe.SimpleTicTacToeViewModel;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class TicTacToe implements Game {
    private final int sz = 10;
    private final int[][] T = new int[sz][sz]; // -1 EMPTY, 0 x, 1 o
    {
        for (int i = 0; i < sz; ++i)
            for (int j = 0; j < sz; ++j)
                T[i][j] = -1;
    }
    private String settings;
    private state currState = state.UNFINISHED;
    private int moveCount = 0, turn;
    private SimpleIntegerProperty moveCountProperty;

    @Override
    public void makeMove(Command move) { // assumes that isMoveLegal(move) returns true
        TicTacToeCommand tttMove = (TicTacToeCommand) move;
        int pl = tttMove.getPlayer();
        int row = tttMove.getRow();
        int col = tttMove.getCol();

        T[row][col] = pl;
        turn = 1 - turn;
        currState = calcState(row,col);

        moveCount++;
        if (moveCountProperty != null)
            moveCountProperty.set(moveCount);
    }

    @Override
    public boolean isMoveLegal(Command move) {
        if (move instanceof TicTacToeCommand tttMove) {
            int pl = tttMove.getPlayer();
            int row = tttMove.getRow();
            int col = tttMove.getCol();
            return settings != null && getState() == state.UNFINISHED
                    && turn == pl && row < sz && col < sz && T[row][col] == -1;
        }
        return false;
    }

    @Override
    public void start(String sett, int seed) {
        if (!possibleSettings().contains(sett))
            throw new IllegalArgumentException("these settings are not permitted");
        settings = sett;
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return currState;
    }

    private state calcState(int row,int col) {
        int winner = -1;
        int pl = T[row][col];
        int tmp_row, tmp_col;
        //check if player won by columns
        int tmp_score = 1;
        tmp_col = col;
        while(tmp_col < sz - 1 && tmp_score < 5){
            tmp_col++;
            if(T[row][tmp_col] == pl){
                tmp_score++;
            }
            else {
                break;
            }
        }
        tmp_col = col;
        while(tmp_col > 0 && tmp_score < 5){
            tmp_col--;
            if(T[row][tmp_col] == pl){
                tmp_score++;
            }
            else {
                break;
            }
        }
        if(tmp_score == 5){
            winner = pl;
        }
        tmp_score = 1;
        //check is player won by rows
        if(winner == -1){
            tmp_row = row;
            while(tmp_row < sz - 1 && tmp_score < 5){
                tmp_row++;
                if(T[tmp_row][col] == pl){
                    tmp_score++;
                }
                else {
                    break;
                }
            }
            tmp_row = row;
            while(tmp_row > 0 && tmp_score < 5) {
                tmp_row--;
                if (T[tmp_row][col] == pl) {
                    tmp_score++;
                } else {
                    break;
                }
            }
            if(tmp_score == 5){
                winner = pl;
            }
        }
        if (winner != -1) {
            return winner == 0 ? state.P0_WON : state.P1_WON;
        }
        //check if player won by diagonal to north east - south west
        tmp_col=col;
        tmp_row=row;
        tmp_score=1;
        while(tmp_row < sz - 1 && tmp_col<sz-1 && tmp_score < 5){
            tmp_row++;
            tmp_col++;
            if(T[tmp_row][tmp_col] == pl){
                tmp_score++;
            }
            else {
                break;
            }
        }
        tmp_row=row;
        tmp_col=col;
        while(tmp_row > 0 && tmp_col>0 && tmp_score < 5){
            tmp_row--;
            tmp_col--;
            if(T[tmp_row][tmp_col] == pl){
                tmp_score++;
            }
            else {
                break;
            }
        }
        if(tmp_score == 5){
            winner = pl;
        }
        System.out.print(tmp_score);
        tmp_score=1;
        tmp_col=col;
        tmp_row=row;
        if(winner==-1){
            while(tmp_row < sz - 1 && tmp_col>0 && tmp_score < 5){
                tmp_row++;
                tmp_col--;
                if(T[tmp_row][tmp_col] == pl){
                    tmp_score++;
                }
                else {
                    break;
                }
            }
            tmp_row=row;
            tmp_col=col;
            while(tmp_row > 0 && tmp_col <sz-1 && tmp_score < 5){
                tmp_row--;
                tmp_col++;
                if(T[tmp_row][tmp_col] == pl){
                    tmp_score++;
                }
                else {
                    break;
                }
            }
            if(tmp_score == 5){
                winner = pl;
            }
        }
        if (winner != -1) {
            return winner == 0 ? state.P0_WON : state.P1_WON;
        }
        int e = 0;
        for (int i = 0; i < sz; ++i){
            for (int j = 0; j < sz; ++j) {
                if (T[i][j] == -1) {
                    ++e;
                }
            }
        }
        return e > 0 ? state.UNFINISHED : state.DRAW;

    }

    @Override
    public String getName() {
        return "Tic tac toe";
    }

    @Override
    public Set<String> possibleSettings() {
        return Set.of("Small","Medium","Big");
    }

    @Override
    public TicTacToeViewModel createViewModel(Client cl, int id) {
        return new TicTacToeViewModel(cl, id, this);
    }

    public int getSize() {
        return sz;
    }

    public int getFieldAt(int i, int j) {
        return T[i][j];
    }

    public int getTurn() {
        return turn;
    }

    public ReadOnlyIntegerProperty getMoveCountProperty() {
        if (moveCountProperty == null)
            moveCountProperty = new SimpleIntegerProperty(moveCount);
        return moveCountProperty;
    }

    @Override
    public String toString() {
        return "TicTacToe{" +
                "sz=" + sz +
                ", T=" + Arrays.deepToString(T) +
                ", settings='" + settings + '\'' +
                ", currState=" + currState +
                ", moveCount=" + moveCount +
                ", turn=" + turn +
                ", moveCountProperty=" + moveCountProperty +
                '}';
    }
}
