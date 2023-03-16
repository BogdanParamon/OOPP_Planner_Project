package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private MFXListView<MFXButton> boards;

    @FXML
    private MFXTextField boardTitle;
    @FXML
    private Text subheading;

    private int index = 0;

    /**
     * Setup server and main controller
     *
     * @param server   server to connect with
     * @param mainCtrl main controller to change scenes
     */
    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * @param url    The location used to resolve relative paths for the root object, or
     *               {@code null} if the location is not known.
     * @param bundle The resources used to localize the root object, or {@code null} if
     *               the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle bundle) {
        ObservableList<MFXButton> items = FXCollections.observableArrayList();
        for (var i : new String[]{"Board 1"}) {
            MFXButton button = new MFXButton(i);
            button.setOnAction(event -> switchSceneToBoard(i));
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
                subheading.setText(text.substring(0, ++index));
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * switch scene to board view and provides name for scene
     *
     * @param name for the specific scene
     */
    public void switchSceneToBoard(String name) {
        mainCtrl.showBoard();
        mainCtrl.getBoard().setBoardName(name);
    }

    public void addBoard() {
        try {
            server.addBoard(getBoard());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        switchSceneToBoard(getBoard().title);
    }

    public Board getBoard() {
        return new Board(boardTitle.getText());
    }

    public void switchSceneToHome() {
        mainCtrl.showHome();
    }

}
