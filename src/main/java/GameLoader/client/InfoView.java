package GameLoader.client;

import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.stream.Stream;

public class InfoView extends GridPane {
    public InfoView(InfoViewModel infoViewModel) {
        int gridSz = 10;

        Stream.generate(RowConstraints::new).limit(gridSz).forEach(getRowConstraints()::add);
        Stream.generate(ColumnConstraints::new).limit(gridSz).forEach(getColumnConstraints()::add);
        getRowConstraints().forEach(c -> { c.setPercentHeight(100); c.setVgrow(Priority.ALWAYS); });
        getColumnConstraints().forEach(c -> { c.setPercentWidth(100); c.setHgrow(Priority.ALWAYS); });

        add(infoViewModel.createGameView(), 0, 0, 7, 10);
    }
}
