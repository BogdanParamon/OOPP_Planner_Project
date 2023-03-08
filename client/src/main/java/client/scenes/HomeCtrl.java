package client.scenes;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeCtrl implements Initializable {
    @FXML private MFXListView<String> boards;

    @FXML private Text subheading;

    private int index = 0;

    public void initialize(URL url, ResourceBundle bundle) {
        boards.setItems(FXCollections.observableArrayList("a","b"));
        subheadingAnimation();
    }

    /**
     * A typing animation for the tagline
     */
    private void subheadingAnimation() {
        Timeline timeline = new Timeline();
        String text = "Your Personal Task List Organiser";
        subheading.setText("");

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), event -> {
            if (index == text.length()) timeline.stop();
            else {
                subheading.setText(text.substring(0,++index));
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
