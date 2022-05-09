package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.PlayView;
import GameLoader.common.Game;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

public class SimpleTicTacToeView extends GridPane implements PlayView {
    private final SimpleTicTacToeViewModel gvm;
    private final SimpleTicTacToe game;

    public SimpleTicTacToeView(SimpleTicTacToeViewModel model) {
        gvm = model;
        game = gvm.getGame();
        int sz = game.getSize();
        int gridSz = sz * 2 - 1;

        Stream.generate(RowConstraints::new).limit(gridSz).forEach(getRowConstraints()::add);
        Stream.generate(ColumnConstraints::new).limit(gridSz).forEach(getColumnConstraints()::add);
        getRowConstraints().forEach(c -> { c.setPercentHeight(100); c.setVgrow(Priority.ALWAYS); });
        getColumnConstraints().forEach(c -> { c.setPercentWidth(100); c.setHgrow(Priority.ALWAYS); });

        for (int i = 0; i < sz; ++i)
            for (int j = 0; j < sz; ++j) {
                Node child = new ClickableField(i, j);
                add(child, i * 2, j * 2);
            }
    }

    private static Paint playerPaint(int player) {
        //if (player == 0)
          //  return Color.RED;
        //if (player == 1)
          //  return Color.BLUE;
        return Color.BLACK;
    }

    private static Background playerBackground(int player) throws FileNotFoundException {
        BackgroundFill fill = new BackgroundFill(playerPaint(player), CornerRadii.EMPTY, Insets.EMPTY);
        //var d=new BackgroundImage(a, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        return new Background(fill);
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