package GameLoader.games.TicTacToe;

import GameLoader.common.Game;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Font;


import java.util.stream.Stream;

public class TicTacToeView extends GridPane {
    private final TicTacToeViewModel gvm;
    private final TicTacToe game;
    private final ReadOnlyIntegerProperty obs;

    public TicTacToeView(TicTacToeViewModel model) {
        gvm = model;
        game = gvm.getGame();
        int sz = game.getSize();
        int gridSz = 2 * sz + 3;
        obs = gvm.getGame().getMoveCountProperty();
        obs.addListener((a, b, c) -> {}); //EXACTLY FUCKING WHY


        Stream.generate(RowConstraints::new).limit(gridSz).forEach(getRowConstraints()::add);
        Stream.generate(ColumnConstraints::new).limit(gridSz).forEach(getColumnConstraints()::add);
        var tmp = getRowConstraints();
        int tmp_i = 0;
        for (var c : tmp) {
            if(tmp_i==0){
                c.setPercentHeight(20);
                c.setVgrow(Priority.ALWAYS);
            }
            else if(tmp_i==gridSz-1||tmp_i==gridSz-2){
                c.setPercentHeight(100);
                c.setVgrow(Priority.ALWAYS);
            }
            else if (tmp_i % 2 == 0) {
                c.setPercentHeight(100);
                c.setVgrow(Priority.ALWAYS);
            } else {
                c.setPercentHeight(20);
                c.setVgrow(Priority.ALWAYS);
            }
            tmp_i++;
        }

        tmp_i = 0;
        var tmp2 = getColumnConstraints();
        for (var c : tmp2) {
            if (tmp_i % 2 == 0) {
                c.setPercentWidth(100);
                c.setHgrow(Priority.ALWAYS);
            } else {
                c.setPercentWidth(20);
                c.setHgrow(Priority.ALWAYS);
            }
            tmp_i++;
        }
        for (int i = 0; i < sz; ++i){
            for (int j = 0; j < sz; ++j) {
                Node child = new ClickableField(i, j);
                add(child, i * 2 + 2, j * 2 + 2);
            }
        }
        for(int i=0;i< gridSz;i++){
            for (int j=0;j<gridSz;j++){
                if(j==gridSz-1){
                    if(i==gridSz/2){

                        continue;
                    }
                }
                if(j%2==1||i%2==1||i==0||j==0||i==gridSz-1||j==gridSz-1){
                    Node child = new NotClickableField();
                    add(child,i,j);
                }
            }
        }
        Node child = new PlayerField();
        add(child,0,gridSz-2,gridSz,2);
    }


    private static Background playerBackground(int player) {
        String tcs = TicTacToe.class.getResource("/images/tcs5.png").toExternalForm();
        String agh = TicTacToe.class.getResource("/images/agh5.png").toExternalForm();
        if(player==0){
            BackgroundImage image=new BackgroundImage(new Image(tcs),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
            BackgroundFill fill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
            return new Background(new BackgroundFill[]{fill},new BackgroundImage[]{image});
        }
        else if (player==1){
            BackgroundImage image=new BackgroundImage(new Image(agh),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
            BackgroundFill fill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
            return new Background(new BackgroundFill[]{fill},new BackgroundImage[]{image});
        }
        else {
            BackgroundFill fill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
            return new Background(fill);
        }

    }
    private static class NotClickableField extends VBox{
        public NotClickableField(){
            BackgroundFill fill = new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY);
            Background tmp = new Background(fill);
            backgroundProperty().set(tmp);
        }
    }
    private class PlayerField extends VBox{
        public PlayerField(){
            Label label=new Label();
            BackgroundFill fill = new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY);
            Background tmp = new Background(fill);
            backgroundProperty().set(tmp);
            label.textProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        switch (game.getState()) {
                            case UNFINISHED -> {
                                return game.getTurn() == gvm.playingAs() ? "Your turn" : "Wait";
                            }
                            case DRAW -> {
                                return "Draw";
                            }
                            case P0_WON, P1_WON -> {
                                int won = game.getState() == Game.state.P0_WON ? 0 : 1;
                                return won == gvm.playingAs() ? "You won" : "You lost";
                            }
                        }
                        throw new RuntimeException();
                    },
                    obs
            ));
            getChildren().add(label);
            label.setFont(new Font("HELVETICA",40));
            setAlignment(Pos.CENTER);
        }
    }
    private class ClickableField extends VBox {
        public ClickableField(int i, int j) {
            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY)
                    gvm.clickedOn(i, j);

            });

            IntegerProperty owner = new SimpleIntegerProperty(-1);
            BooleanProperty clickable = new SimpleBooleanProperty(game.getTurn() == gvm.playingAs());

            game.getMoveCountProperty().addListener((obs, oldVal, newVal) -> {
                int newOwner = game.getFieldAt(i, j);
                owner.setValue(newOwner);

                boolean newClickable = newOwner == -1 && game.getState() == Game.state.UNFINISHED
                        && game.getTurn() == gvm.playingAs();
                clickable.setValue(newClickable);
            });

            backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> playerBackground(owner.get()),
                            owner
                    )

            );

            cursorProperty().bind(
                    Bindings.createObjectBinding(
                            () -> clickable.get() ? Cursor.HAND : Cursor.DEFAULT,
                            clickable
                    )
            );

        }
    }
}