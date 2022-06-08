package GameLoader.games.DotsAndBoxes;

import GameLoader.common.Game;
import static GameLoader.common.UtilityGUI.HBoxFixedRatio;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DotsAndBoxesView extends VBox { // TODO refactor everything
    private final DotsAndBoxesViewModel gvm;
    private final DotsAndBoxes game;
    private final ReadOnlyIntegerProperty obs;

    private static final Background emptyEdge = getBackgroundFromColor(Color.LIGHTGREY);
    private static final Background lastEdge = getBackgroundFromColor(Color.DARKGREY);
    private static final Background markedEdge = getBackgroundFromColor(Color.GREY); // GREY is darker than DARKGREY xd
    private static final Background[] squareColor = new Background[]{
            getBackgroundFromColor(Color.WHITE), // empty square
            getBackgroundFromColor(Color.BLUE), // p0 square
            getBackgroundFromColor(Color.RED) // p1 square
    };

    private static final double WIDE = 50, NARROW = 10;

    public DotsAndBoxesView(DotsAndBoxesViewModel model) {
        gvm = model;
        game = gvm.getGame();
        obs = gvm.getGame().getMoveCountProperty();
        obs.addListener((a, b, c) -> {}); // FUCKING WHY ? #2

        double HtoWRatio = (game.getSize().row() * (WIDE + NARROW) + NARROW) /
                (game.getSize().col() * (WIDE + NARROW) + NARROW);

        HBox header = new Header();
        HBox gamePane = new HBoxFixedRatio(new GamePane(), HtoWRatio);
        HBox footer = new Footer();

        setAlignment(Pos.CENTER);

        getChildren().addAll(header, gamePane, footer);
        setVgrow(gamePane, Priority.ALWAYS);
    }

    private class Header extends HBox {
        public Header() {
            Label l = new Label();

            l.textProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        switch (game.getState()) {
                            case UNFINISHED -> {
                                return game.getTurn() == gvm.playingAs() ? "Your turn" : "Opponent's turn";
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

            l.textFillProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        switch (game.getState()) {
                            case UNFINISHED -> {
                                return squareColor[game.getTurn()+1].getFills().get(0).getFill();
                            }
                            case DRAW -> {
                                return Color.BLACK;
                            }
                            case P0_WON, P1_WON -> {
                                int won = game.getState() == Game.state.P0_WON ? 0 : 1;
                                return squareColor[won+1].getFills().get(0).getFill();
                            }
                        }
                        throw new RuntimeException();
                    },
                    obs
            ));

            l.setFont(new Font(null, 24));

            setAlignment(Pos.CENTER);
            getChildren().add(l);
        }
    }

    private class Footer extends HBox {
        public Footer() {
            VBox[] spacer = new VBox[]{new VBox(), new VBox(), new VBox()};
            Label[] sc = new Label[]{new Label(), new Label()};

            for (int i = 0; i < 2; ++i) {
                final int fi = i;

                sc[i].textProperty().bind(Bindings.createObjectBinding(
                        () -> "P"+fi+" score: " + game.getScore(fi),
                        obs
                ));

                sc[i].setTextFill(squareColor[i+1].getFills().get(0).getFill()); // TODO clean

                sc[i].setFont(new Font(null, 24));
            }

            setAlignment(Pos.CENTER);
            getChildren().addAll(spacer[0], sc[0], spacer[1], sc[1], spacer[2]);

            for (VBox v : spacer)
                setHgrow(v, Priority.ALWAYS);

        }
    }

    private class GamePane extends GridPane {
        public GamePane() {
            int gridRows = game.getSize().row()*2+1;
            int gridCols = game.getSize().col()*2+1;

            for (int i = 0; i < gridRows; ++i) {
                RowConstraints rc = new RowConstraints();
                rc.setPercentHeight(i % 2 == 0 ? NARROW : WIDE);
                rc.setVgrow(Priority.ALWAYS);
                getRowConstraints().add(rc);
            }

            for (int i = 0; i < gridCols; ++i) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(i % 2 == 0 ? NARROW : WIDE);
                cc.setHgrow(Priority.ALWAYS);
                getColumnConstraints().add(cc);
            }

            for (int i = 0; i < gridRows; ++i)
                for (int j = 0; j < gridCols; ++j) {
                    DotsAndBoxes.Coord c = new DotsAndBoxes.Coord(i, j);

                    VBox set = c.isEdge() ? new EdgeField(c) :
                            c.isSquare() ? new SquareField(c) : new PointField(c);

                    add(set, j, i); // FUCKING WHY ?
                }
        }
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
                            () -> !game.isAlone(c) ? markedEdge : emptyEdge,
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
