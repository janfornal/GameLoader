package GameLoader.games.DotsAndBoxes;

import GameLoader.client.PlayView;
import GameLoader.common.Game;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.stream.Stream;

public class DotsAndBoxesView extends GridPane implements PlayView {
    private final DotsAndBoxesViewModel gvm;
    private final DotsAndBoxes game;
    private final ReadOnlyIntegerProperty obs;

    private static final Background emptyEdge = getBackgroundFromColor(Color.WHITE);
    private static final Background markedEdge = getBackgroundFromColor(Color.GREY);
    private static final Background lastEdge = getBackgroundFromColor(Color.LIGHTGREY);
    private static final Background[] squareColor = new Background[]{
            getBackgroundFromColor(Color.LIME), // empty square
            getBackgroundFromColor(Color.BLUE), // p0 square
            getBackgroundFromColor(Color.RED) // p1 square
    };

    private static final double WIDE = 50, NARROW = 10;

    public DotsAndBoxesView(DotsAndBoxesViewModel model) {
        gvm = model;
        game = gvm.getGame();
        obs = gvm.getGame().getMoveCountProperty();
        obs.addListener((a, b, c) -> {}); // FUCKING WHY ? #2

        GridPane inner = new GridPane();

        int gridRows = game.getSize().row()*2+1;
        int gridCols = game.getSize().col()*2+1;

        for (int i = 0; i < gridRows; ++i) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(i % 2 == 0 ? NARROW : WIDE);
            rc.setVgrow(Priority.ALWAYS);
            inner.getRowConstraints().add(rc);
        }

        for (int i = 0; i < gridCols; ++i) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(i % 2 == 0 ? NARROW : WIDE);
            cc.setHgrow(Priority.ALWAYS);
            inner.getColumnConstraints().add(cc);
        }

        double HtoWRatio = (gridRows * (WIDE + NARROW) + NARROW) / (gridCols * (WIDE + NARROW) + NARROW);

        SimpleDoubleProperty sizeW = new SimpleDoubleProperty(10);

        inner.prefWidthProperty().bindBidirectional(inner.prefHeightProperty());

        for (int i = 0; i < gridRows; ++i)
            for (int j = 0; j < gridCols; ++j) {
                DotsAndBoxes.Coord c = new DotsAndBoxes.Coord(i, j);

                VBox set = c.isEdge() ? new EdgeField(c) :
                        c.isSquare() ? new SquareField(c) : new PointField(c);

                inner.add(set, j, i); // FUCKING WHY ?
            }

        Stream.generate(RowConstraints::new).limit(3).forEach(getRowConstraints()::add);
        Stream.generate(ColumnConstraints::new).limit(3).forEach(getColumnConstraints()::add);
        getRowConstraints().forEach(c -> { c.setPercentHeight(100); c.setVgrow(Priority.ALWAYS); });
        getColumnConstraints().forEach(c -> { c.setPercentWidth(100); c.setHgrow(Priority.ALWAYS); });
        add(inner, 1, 1);
    }

    private static Background getBackgroundFromColor(Color c) {
        return new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private class PointField extends VBox {
        public PointField(DotsAndBoxes.Coord c) {
            if (!c.isPoint())
                throw new RuntimeException(c.toString());

            backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> game.isSurrounded(c) ? markedEdge : emptyEdge,
                            obs
                    )
            );
        }
    }

    private class SquareField extends VBox {
        public SquareField(DotsAndBoxes.Coord c) {
            if (!c.isSquare())
                throw new RuntimeException(c.toString());

            backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> squareColor[game.getOwner(c) + 1],
                            obs
                    )
            );
        }
    }

    private class EdgeField extends VBox {
        public EdgeField(DotsAndBoxes.Coord c) {
            if (!c.isEdge())
                throw new RuntimeException(c.toString());

            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY)
                    gvm.clickedOn(c);
            });

            backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> {
                                if (!game.isMarked(c))
                                    return emptyEdge;
                                if (c.equals(game.mostRecentMarking()) && game.getState() == Game.state.UNFINISHED)
                                    return lastEdge;
                                return markedEdge;
                            },
                            obs
                    )
            );

            cursorProperty().bind(
                    Bindings.createObjectBinding(
                            () -> !game.isMarked(c) && game.getTurn() == gvm.playingAs() ? Cursor.HAND : Cursor.DEFAULT,
                            obs
                    )
            );
        }
    }

}
