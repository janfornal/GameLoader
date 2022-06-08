package GameLoader.games.PaperSoccer;

import GameLoader.common.Game;
import static GameLoader.common.UtilityGUI.HBoxFixedRatio;
import static GameLoader.common.Utility.IntPair;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.Math.max;

public class PaperSoccerView extends VBox { // TODO refactor everything
    private final PaperSoccerViewModel gvm;
    private final PaperSoccer game;
    private final ReadOnlyIntegerProperty obs;

    private static final double wideGridSquare = 10, narrowGridSquare = 50;
    private static final double borderThickness = 3, movedThickness = 1.75, activeThickness = 0.5;
    private static final Color movedColor = Color.BLACK, activeColor = Color.DARKGREY, ballColor = Color.BLACK;

    private static final Color normalBorderColor = Color.BLACK;
    private static final Color[] playerBorderColor = new Color[]{Color.BLUE, Color.RED};

    public PaperSoccerView(PaperSoccerViewModel model) {
        gvm = model;
        game = gvm.getGame();
        obs = gvm.getGame().getMoveCountProperty();
        obs.addListener((a, b, c) -> {});

        IntPair clc = calcSz();
        double HtoWRatio = (clc.y() * (wideGridSquare + narrowGridSquare) - wideGridSquare) /
                (clc.x() * (wideGridSquare + narrowGridSquare) - wideGridSquare);

        HBox header = getHeader();
        HBox gamePane = new HBoxFixedRatio(getBoardGUI(), HtoWRatio);

        setAlignment(Pos.CENTER);

        getChildren().addAll(header, gamePane);
        setVgrow(gamePane, Priority.ALWAYS);
    }

    private HBox getHeader() {
        return new HBox() {{
            Label l = new Label();

            l.textProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        switch (game.getState()) {
                            case UNFINISHED -> {
                                return game.getTurn() == gvm.playingAs() ? "Your turn" : "Opponent's turn";
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
                                return playerBorderColor[game.getTurn()];
                            }
                            case DRAW -> {
                                return Color.BLACK;
                            }
                            case P0_WON, P1_WON -> {
                                int won = game.getState() == Game.state.P0_WON ? 0 : 1;
                                return playerBorderColor[won];
                            }
                        }
                        throw new RuntimeException();
                    },
                    obs
            ));

            l.setFont(new Font(null, 24));

            setAlignment(Pos.CENTER);
            getChildren().add(l);
        }};
    }

    private IntPair calcSz() {
        int maxx = 0, maxy = 0;
        for (PaperSoccer.Field f : game.getAllFields()) {
            maxx = max(maxx, f.pos.x());
            maxy = max(maxy, f.pos.y());
        }
        return new IntPair(2*maxx+1, 2*maxy+1);
    }

    private GridPane getBoardGUI() {
        return new GridPane() {{
            IntPair clc = calcSz();
            int gridRows = clc.y()*2-1;
            int gridCols = clc.x()*2-1;

            for (int i = 0; i < gridRows; ++i) {
                RowConstraints rc = new RowConstraints();
                rc.setPercentHeight(i % 2 == 0 ? narrowGridSquare : wideGridSquare);
                rc.setVgrow(Priority.ALWAYS);
                getRowConstraints().add(rc);
            }

            for (int i = 0; i < gridCols; ++i) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(i % 2 == 0 ? narrowGridSquare : wideGridSquare);
                cc.setHgrow(Priority.ALWAYS);
                getColumnConstraints().add(cc);
            }

            Map<IntPair, VBox> fields = new HashMap<>();
            for (PaperSoccer.Field f : game.getAllFields()) {
                VBox guiField = getGUIField(f);
                fields.put(f.pos, guiField);

                guiField.toFront();
                boolean rev = gvm.playingAs() == 0;

                int x = (rev ? -1 : 1) * f.pos.x() + clc.x() / 2;
                int y = (rev ? -1 : 1) * f.pos.y() + clc.y() / 2;
                add(guiField, 2 * x, 2 * y);
            }

            getChildren().add(0, getGUIBall(fields));

            for (PaperSoccer.Edge e : game.getAllEdges())
                getChildren().add(0, getGUIEdge(e, fields));
        }};
    }

    private VBox getGUIField(PaperSoccer.Field f) {
        return new VBox() {{
            Supplier<Boolean> clickable = () -> {
                if (game.getState() != Game.state.UNFINISHED || game.getTurn() != gvm.playingAs())
                    return false;
                Integer d = PaperSoccer.calcDir(f, game.getCurrField());
                return d != null && f.edges[d] != null && f.edges[d].active;
            };

            cursorProperty().bind(
                    Bindings.createObjectBinding(
                            () -> gvm.getModelUser().currentPlayModel == gvm && clickable.get() ?
                                    Cursor.HAND : Cursor.DEFAULT,
                            obs
                    )
            );

            setOnMouseClicked(event -> {
                if (gvm.getModelUser().currentPlayModel == gvm && event.getButton() == MouseButton.PRIMARY && clickable.get())
                    gvm.userCmd(new PaperSoccerCommand(gvm.playingAs(), PaperSoccer.calcDir(game.getCurrField(), f)));
            });
        }};
    }

    private Line getGUIEdge(PaperSoccer.Edge e, Map<IntPair, VBox> fields) {
        return new Line() {{
            VBox a = fields.get(e.f.pos);
            VBox b = fields.get(e.g.pos);

            startXProperty().bind(a.layoutXProperty().add(a.widthProperty().divide(2)));
            startYProperty().bind(a.layoutYProperty().add(a.heightProperty().divide(2)));
            endXProperty().bind(b.layoutXProperty().add(b.widthProperty().divide(2)));
            endYProperty().bind(b.layoutYProperty().add(b.heightProperty().divide(2)));

//            toBack(); // FIXME does this do something?

            setManaged(false);

            if (e.border) {
                setStrokeWidth(borderThickness);
                setStroke(e.goalBorder == -1 ? normalBorderColor : playerBorderColor[e.goalBorder]);
            }
            else {
                strokeWidthProperty().bind(
                        Bindings.createObjectBinding(
                                () -> e.active ? activeThickness : movedThickness,
                                obs
                        )
                );
                strokeProperty().bind(
                        Bindings.createObjectBinding(
                                () -> e.active ? activeColor : movedColor,
                                obs
                        )
                );
            }
        }};
    }

    private Circle getGUIBall(Map<IntPair, VBox> fields) {
        return new Circle() {{
            VBox any = fields.get(game.getCurrField().pos);
            radiusProperty().bind(any.widthProperty().divide(4));

            centerXProperty().bind(
                    Bindings.createObjectBinding(
                            () -> {
                                VBox curr = fields.get(game.getCurrField().pos);
                                return curr.getLayoutX() + curr.getWidth() / 2;
                            },
                            obs, any.widthProperty(), any.heightProperty()
                    )
            );
            centerYProperty().bind(
                    Bindings.createObjectBinding(
                            () -> {
                                VBox curr = fields.get(game.getCurrField().pos);
                                return curr.getLayoutY() + curr.getHeight() / 2;
                            },
                            obs, any.widthProperty(), any.heightProperty()
                    )
            );

            setManaged(false);

            setFill(ballColor);
        }};
    }
}