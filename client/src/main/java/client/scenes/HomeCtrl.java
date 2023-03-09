package client.scenes;

import client.utils.ServerUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import com.google.inject.Inject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML private MFXListView<MFXButton> boards;

    @FXML private Text subheading;

    private int index = 0;

    @Inject
    public HomeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    public void initialize(URL url, ResourceBundle bundle) {
        ObservableList<MFXButton> items = FXCollections.observableArrayList();
        for (var i : new String[]{"Board 1", "Board 2"}) {
            MFXButton button = new MFXButton(i);
            button.setOnAction(event -> switchSceneToBoard());
            items.add(button);
        }
        
        boards.setItems(items);
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

    public void switchSceneToBoard() {
        mainCtrl.showBoard();
    }

}
