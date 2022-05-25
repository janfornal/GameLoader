package GameLoader.games.PaperSoccer;

import GameLoader.client.PlayView;
import GameLoader.common.Game;
import GameLoader.common.HBoxFixedRatio;
import GameLoader.common.IntPair;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;

public class PaperSoccerView extends VBox implements PlayView { // TODO refactor everything
    private final PaperSoccerViewModel gvm;
    private final PaperSoccer game;
    private final ReadOnlyIntegerProperty obs;

    private static final double WIDE = 50, NARROW = 10;

    public PaperSoccerView(PaperSoccerViewModel model) {
        gvm = model;
        game = gvm.getGame();
        obs = gvm.getGame().getMoveCountProperty();
        obs.addListener((a, b, c) -> {});

        double HtoWRatio = 1;

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

            l.setFont(new Font(null, 24));

            setAlignment(Pos.CENTER);
            getChildren().add(l);
        }};
    }

    private GridPane getBoardGUII() {
        return new GridPane() {{
            int gridRows = 7;
            int gridCols = 7;

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

            VBox[][] arr = new VBox[gridRows][gridCols];

            for (int i = 0; i < gridRows; ++i)
                for (int j = 0; j < gridCols; ++j) {
                    int finalJ = j;
                    int finalI = i;
                    VBox at = new VBox() {{
                        if (finalI % 2 == finalJ % 2)
                            setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    }};
                    arr[i][j] = at;
                    add(at, j, i);
                }
            Line l = conn(arr[1][1], arr[2][3]);
            l.setManaged(false);
            getChildren().add(l);
            System.out.println(getChildren());

            widthProperty().addListener((a, b, c) -> {
                ((SimpleIntegerProperty) obs).set(obs.get()^1);
            });
        }};
    }

    private Line conn(VBox a, VBox b) {
        return new Line() {{
//            obs.addListener((____,__,___) -> {
//                setStartX(a.getLayoutX());
//                setStartY(a.getLayoutY());
//                setEndX(b.getLayoutX());
//                setEndY(b.getLayoutY());
//                System.out.println("CH: " + a.getLayoutX() + ", " + a.getLayoutY() + ", " + b.getLayoutX() + ", " + b.getLayoutY());
//            });
            startXProperty().bind(a.layoutXProperty().add(a.widthProperty().divide(2)));
            startYProperty().bind(a.layoutYProperty().add(a.heightProperty().divide(2)));
            endXProperty().bind(b.layoutXProperty().add(b.widthProperty().divide(2)));
            endYProperty().bind(b.layoutYProperty().add(b.heightProperty().divide(2)));
//            startXProperty().bind(Bindings.createObjectBinding(
//                    () -> a.getLayoutX(),
//                    obs
//            ));
//            startYProperty().bind(Bindings.createObjectBinding(
//                    () -> a.getLayoutY(),
//                    obs
//            ));
//            endXProperty().bind(Bindings.createObjectBinding(
//                    () -> b.getLayoutX(),
//                    obs
//            ));
//            endYProperty().bind(Bindings.createObjectBinding(
//                    () -> b.getLayoutY(),
//                    obs
//            ));
        }};
    }

    private GridPane getBoardGUI() {
        return new GridPane() {{
            int maxx = 0, maxy = 0;
            for (PaperSoccer.Field f : game.getAllFields()) {
                maxx = max(maxx, f.pos.x());
                maxy = max(maxy, f.pos.y());
            }

            int nodesx = 2*maxx+1;
            int nodesy = 2*maxy+1;

            int gridRows = nodesy*2-1;
            int gridCols = nodesx*2-1;

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

            Map<IntPair, VBox> fields = new HashMap<>();
            for (PaperSoccer.Field f : game.getAllFields()) {
                VBox guiField = getGUIField(f);
                fields.put(f.pos, guiField);

                int x = f.pos.x()+maxx;
                int y = f.pos.y()+maxy;
                add(guiField, 2*x, 2*y);
            }

            for (PaperSoccer.Edge e : game.getAllEdges())
                getChildren().add(getGUIEdge(e, fields));
        }};
    }

    private VBox getGUIField(PaperSoccer.Field f) {
        return new VBox() {{
            backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)),
                            obs
                    )
            );
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

            if ()

            setManaged(false);
        }};
    }
}