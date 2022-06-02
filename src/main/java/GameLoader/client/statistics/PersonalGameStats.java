package GameLoader.client.statistics;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import org.controlsfx.control.SegmentedBar;

public class PersonalGameStats {

    private SegmentedBar.Segment greySegment = new SegmentedBar.Segment(100, "0");
    private SegmentedBar.Segment greenSegment = new SegmentedBar.Segment(0, "0");
    private SegmentedBar.Segment blueSegment = new SegmentedBar.Segment(0, "0");
    private SegmentedBar.Segment redSegment = new SegmentedBar.Segment(0, "0");

    @FXML
    private Label eloLabel;

    @FXML
    private Label gameLabel;

    @FXML
    private SegmentedBar<SegmentedBar.Segment> wonToLostBar;

    @FXML
    void initialize() {
        wonToLostBar.setOrientation(Orientation.HORIZONTAL);
        wonToLostBar.getSegments().addAll(
            greySegment, greenSegment, blueSegment, redSegment
        );
        wonToLostBar.setSegmentViewFactory(segment -> {
            SegmentedBar<SegmentedBar.Segment>.SegmentView view = wonToLostBar.new SegmentView(segment);
            String color;
            if(segment.equals(greySegment)) {
                color = "#8E8E8E";
            }
            else if(segment.equals(greenSegment)) {
                color = "#32CD32";
            }
            else if(segment.equals(blueSegment)) {
                color = "#63C5DA";
            }
            else if(segment.equals(redSegment)) {
                color = "#E34234";
            }
            else throw new RuntimeException("unable to find right segment");
            view.setStyle("-fx-background-color: "+color);
            return view;
        });

    }

    public void setGameLabel(String key) {
        gameLabel.setText("Game: " + key);
    }

    public void setEloLabel(int key) {
        eloLabel.setText("Elo: " + key);
    }

    public void setBar(int won, int draw, int lost) {
        greenSegment.setValue((double) won);
        greenSegment.setText("" + won);
        blueSegment.setValue((double) draw);
        blueSegment.setText("" + draw);
        redSegment.setValue((double) lost);
        redSegment.setText("" + lost);
    }
}
