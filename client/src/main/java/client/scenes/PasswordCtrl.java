package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.util.Duration.millis;

public class PasswordCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Board board;

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField passwordField;

    @FXML
    private Text text;

    @FXML
    private Text errorMsg;

    @Inject
    public PasswordCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCtrl.initHeader(root);
    }


    public void setText(String text) {
        this.text.setText(text);
    }

    public void ok() {
        if (Objects.equals(passwordField.getText(), board.getPassword()))
            switchSceneToBoard(board);
        else {
            mainCtrl.password.getStylesheets().add("/client/styles/inputerror.css");
            errorMsg.setVisible(true);

            Timeline timeline = new Timeline(
                    new KeyFrame(millis(0), new KeyValue(passwordField.translateXProperty(), 0)),
                    new KeyFrame(millis(50), new KeyValue(passwordField.translateXProperty(), -10)),
                    new KeyFrame(millis(100), new KeyValue(passwordField.translateXProperty(), 10)),
                    new KeyFrame(millis(150),
                            new KeyValue(passwordField.translateXProperty(), -10)),
                    new KeyFrame(millis(200), new KeyValue(passwordField.translateXProperty(), 0))
            );
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    public void goBack() {
        mainCtrl.showBoardOverview();
    }

    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.showBoard();
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
