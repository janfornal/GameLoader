package GameLoader.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.*;

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

    Map<Double, Font> steelCityComicCache = new HashMap<>();
    String steelCityComicURL = UtilityGUI.class.getResource("/steelCityComic.ttf").toExternalForm();

    static Font getSteelCityComic(double sz) {
        if (!steelCityComicCache.containsKey(sz))
            steelCityComicCache.put(sz, Font.loadFont(steelCityComicURL, sz));
        return steelCityComicCache.get(sz);
    }

    static void toSteelCityComic(List<Property<Font>> properties) {
        for (Property<Font> p : properties)
            p.setValue(getSteelCityComic(p.getValue().getSize()));
    }

}
