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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
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

    @FXML
    private AnchorPane root;

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
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        mainCtrl.initHeader(root);
    }

    public void registerForBoardUpdates() {
        server.registerForMessages("/topic/boards/add", ArrayList.class, board -> {
            Platform.runLater(() -> {
                MFXButton button = new MFXButton((String) board.get(1));
                button.setOnAction(event
                        -> switchSceneToBoard(server.getBoardById((long)(int) board.get(0))));
                boards.getItems().add(button);
                boardTitle.clear();
            });
        });
    }

    public void load() {
        var boardTitlesAndIds = server.getBoardTitlesAndIds();
        boards.getItems().clear();
        boardTitlesAndIds.forEach((aLong, s) -> {
            MFXButton button = new MFXButton(s);
            button.setOnAction(event -> switchSceneToBoard(server.getBoardById(aLong)));
            boards.getItems().add(button);
        });
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


    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.showBoard();
    }

    public void addBoard() {
        try {
            Board board = new Board(boardTitle.getText());
            server.send("/app/boards/add", board);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void deleteAll() {
        server.deleteAllBoards();
        boards.getItems().clear();
    }


    public void switchSceneToHome() {
        server.disconnectWebsocket();
        mainCtrl.showHome();
    }

}
