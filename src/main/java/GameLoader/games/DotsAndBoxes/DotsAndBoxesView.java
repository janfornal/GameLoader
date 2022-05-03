package GameLoader.games.DotsAndBoxes;

import GameLoader.client.PlayView;
import GameLoader.common.Game;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DotsAndBoxesView extends GridPane implements PlayView {
    private final DotsAndBoxesViewModel gvm;
    private final DotsAndBoxes game;
    private final ReadOnlyIntegerProperty obs;

    private static final Background emptyEdge = getBackgroundFromColor(Color.WHITE);
    private static final Background markedEdge = getBackgroundFromColor(Color.GREY);
    private static final Background lastEdge = getBackgroundFromColor(Color.LIGHTGREY);
    private static final Background[] squareColor = new Background[]{
            getBackgroundFromColor(Color.GREY), // empty square
            getBackgroundFromColor(Color.BLUE), // p0 square
            getBackgroundFromColor(Color.RED) // p1 square
    };

    public DotsAndBoxesView(DotsAndBoxesViewModel model) {
        gvm = model;
        game = gvm.getGame();
        obs = gvm.getGame().getMoveCountProperty();

        int gridRows = game.getSize().row()*2+1;
        int gridCols = game.getSize().col()*2+1;

        for (int i = 0; i < gridRows; ++i) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(i % 2 == 0 ? 10 : 50);
            rc.setVgrow(Priority.ALWAYS);
            getRowConstraints().add(rc);
        }

        for (int i = 0; i < gridCols; ++i) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(i % 2 == 0 ? 10 : 50);
            cc.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(cc);
        }

        System.out.println(getRowConstraints());
        System.out.println(getColumnConstraints());

        for (int i = 0; i < gridRows; ++i)
            for (int j = 0; j < gridCols; ++j) {
                DotsAndBoxes.Coord c = new DotsAndBoxes.Coord(i, j);

                VBox set = c.isEdge() ? new EdgeField(c) :
                        c.isSquare() ? new SquareField(c) : new PointField(c);

                add(set, j, i); // FUCKING WHY ?
                System.err.println("at " + c + " is " + set.getClass() + " of color " + set.backgroundProperty().get().getFills().get(0).getFill().toString());
            }

        System.err.println(game);
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
