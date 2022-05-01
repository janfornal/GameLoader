package GameLoader.games.DotsAndBoxes;

import GameLoader.client.PlayView;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.List;

public class DotsAndBoxesView extends GridPane implements PlayView {

    public DotsAndBoxesView(DotsAndBoxesViewModel gvm) {

        setPadding(new Insets(10, 20, 10, 20));

        List<Integer> columnWidth = Arrays.asList(100, 100, 100, 80, 140, 80);
        this.getColumnConstraints().addAll(columnWidth.stream()
                .map(t -> new ColumnConstraints()).toList());

        for(int i=0; i<columnWidth.size(); i++) {
            this.getColumnConstraints().get(i).setPercentWidth(columnWidth.get(i));
        }

        List <Integer> rowHeight = Arrays.asList(30, 40, 30, 50, 50, 50, 50, 100);
        this.getRowConstraints().addAll(rowHeight.stream()
                .map(t -> new RowConstraints()).toList());

        for(int i=0; i<rowHeight.size(); i++) {
            this.getRowConstraints().get(i).setPercentHeight(rowHeight.get(i));
        }

        gvm.setElements(new DotsAndBoxesViewModel.guiElements(
                new Label(""),
                new Label("Score: 0"),
                new Label("Score: 0"),
                new GridPane()
        ));

        add(gvm.getElements().stateOfGame(), 1, 1, 2, 1);
        gvm.getElements().stateOfGame().setTextAlignment(TextAlignment.CENTER);
//        gvm.showActualStateHandler(gvm.getElements().stateOfGame());  --to do

        add(gvm.getElements().ourScore(), 4, 3, 1, 1);
        gvm.getElements().ourScore().setTextAlignment(TextAlignment.CENTER);
//        gvm.showOurScoreHandler(gvm.getElements().ourScore());  --to do

        add(gvm.getElements().enemyScore(), 4, 5, 1, 1);
        gvm.getElements().enemyScore().setTextAlignment(TextAlignment.CENTER);
//        gvm.showEnemyScoreHandler(gvm.getElements().enemyScore());  --to do

        add(gvm.getElements().board(), 1, 3, 2, 4);
//        gvm.MakeMoveHandler(gvm.getElements().board());  --to do


    }
}
