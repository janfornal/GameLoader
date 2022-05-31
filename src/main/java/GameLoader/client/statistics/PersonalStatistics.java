package GameLoader.client.statistics;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.SegmentedBar;

import java.util.AbstractMap;

public class PersonalStatistics {

    @FXML
    private AnchorPane personalStatistics;

    @FXML
    private Label titleLabel;

    @FXML
    void initialize() {
//        SegmentedBar<SegmentedBar.Segment> segments = new SegmentedBar<>();
//        segments.setOrientation(Orientation.HORIZONTAL);
//        segments.getSegments().addAll(
//                new SegmentedBar.Segment(10, "10"),
//                new SegmentedBar.Segment(90, "90")
//        );
//        segments.setSegmentViewFactory(segment -> {
//            SegmentedBar<SegmentedBar.Segment>.SegmentView view = segments.new SegmentView(segment);
//            String color = segment.getValue() < 50 ? "#66C2A5" : "#FC8D62" ;
//            view.setStyle("-fx-background-color: "+color);
//            return view ;
//        });
//
//        personalStatistics.getChildren().add(segments);
//        AnchorPane.setLeftAnchor(segments, 0.0);
//        AnchorPane.setTopAnchor(segments, 200.0);
    }

}
