package GameLoader.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public interface UtilityGUI {
    class HBoxFixedRatio extends HBox {
        public HBoxFixedRatio(Pane pain, double HtoWRatio) {
            SimpleDoubleProperty width = new SimpleDoubleProperty(10);

            pain.maxHeightProperty().bind(width.multiply(HtoWRatio));
            pain.maxWidthProperty().bind(width);

            width.bind(Bindings.min(heightProperty().divide(HtoWRatio), widthProperty()));

            setAlignment(Pos.CENTER);
            getChildren().add(pain);
            setHgrow(pain, Priority.ALWAYS);
        }
    }
}
