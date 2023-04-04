package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminOverviewCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private MFXListView<MFXButton> boards;
    @FXML
    private Text subheading;
    @FXML
    private Text boardIdLabel;
    @FXML
    private Text boardTitleLabel;
    @FXML
    private Text boardPasswordLabel;
    @FXML
    private Text boardIdText;
    @FXML
    private Text boardTitleText;
    @FXML
    private Text boardPasswordText;
    @FXML
    private MFXTextField boardTitleField;
    @FXML
    private MFXTextField boardPasswordField;
    @FXML
    private MFXButton editTitleButton;
    @FXML
    private MFXButton saveTitleButton;
    @FXML
    private MFXButton editPasswordButton;
    @FXML
    private MFXButton savePasswordButton;
    @FXML
    private MFXButton viewBoardButton;
    @FXML
    private MFXButton deleteBoardButton;
    @FXML
    private AnchorPane root;

    private int index = 0;
    private long selectedBoardId;

    @Inject
    public AdminOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCtrl.initHeader(root);
    }

    public void load() {
        deselectBoard();

        var boardTitlesAndIds = server.getBoardTitlesAndIds();
        boards.getItems().clear();
        boardTitlesAndIds.forEach((boardId, boardName) -> {
            MFXButton button = new MFXButton(boardName + " #id: " + boardId);
            button.setOnAction(event -> showBoardDetails(boardId));
            boards.getItems().add(button);
        });

        subheadingAnimation();
    }

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

    public void showBoardDetails(Long boardId) {
        Board board = server.getBoardById(boardId);
        selectedBoardId = board.boardId;

        boardIdText.setText(String.valueOf(board.boardId));
        boardTitleText.setText(board.title);
        boardPasswordText.setText("Placeholder password");

        boardTitleField.setText(board.title);
        boardPasswordField.setText("Placeholder password");

        boardIdLabel.setVisible(true);
        boardTitleLabel.setVisible(true);
        boardPasswordLabel.setVisible(true);

        boardIdText.setVisible(true);
        boardTitleText.setVisible(true);
        boardTitleField.setVisible(false);
        editTitleButton.setVisible(true);
        saveTitleButton.setVisible(false);

        boardPasswordText.setVisible(true);
        boardPasswordField.setVisible(false);
        editPasswordButton.setVisible(true);
        savePasswordButton.setVisible(false);

        viewBoardButton.setVisible(true);
        deleteBoardButton.setVisible(true);
        viewBoardButton.setOnAction(event -> switchSceneToBoard(board));
        deleteBoardButton.setOnAction(event -> deleteBoard(board.boardId));
    }

    public void startEditingTitle() {
        boardTitleText.setVisible(false);
        boardTitleField.setVisible(true);
        editTitleButton.setVisible(false);
        saveTitleButton.setVisible(true);
    }

    public void finishEditingTitle() {
        if (!boardTitleField.getText().isEmpty()) {
            String newTitle = boardTitleField.getText();
            server.send("/app/boards/rename/" + selectedBoardId, newTitle);
            boardTitleText.setText(newTitle);
            for (MFXButton button : boards.getItems()) {
                if (button.getText().split(" #id: ")[1].equals(String.valueOf(selectedBoardId))) {
                    button.setText(newTitle + " #id: " + selectedBoardId);
                }
            }
        }
        boardTitleText.setVisible(true);
        boardTitleField.setVisible(false);
        editTitleButton.setVisible(true);
        saveTitleButton.setVisible(false);
    }

    public void startEditingPassword() {
        boardPasswordText.setVisible(false);
        boardPasswordField.setVisible(true);
        editPasswordButton.setVisible(false);
        savePasswordButton.setVisible(true);
    }

    public void finishEditingPassword() {
        boardPasswordText.setVisible(true);
        boardPasswordField.setVisible(false);
        editPasswordButton.setVisible(true);
        savePasswordButton.setVisible(false);
    }

    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.boardCtrl.setAdminMode(true);
        mainCtrl.showBoard();
    }

    public void deleteBoard(long boardId) {
        server.deleteBoardById(boardId);
        MFXButton toBeRemoved = null;
        for (MFXButton button : boards.getItems()) {
            if (button.getText().split(" #id: ")[1].equals(String.valueOf(selectedBoardId))) {
                toBeRemoved = button;
            }
        }
        boards.getItems().remove(toBeRemoved);
        deselectBoard();
    }

    public void deselectBoard() {
        boardIdLabel.setVisible(false);
        boardTitleLabel.setVisible(false);
        boardPasswordLabel.setVisible(false);

        boardIdText.setVisible(false);
        boardTitleText.setVisible(false);
        boardTitleField.setVisible(false);
        editTitleButton.setVisible(false);
        saveTitleButton.setVisible(false);

        boardPasswordText.setVisible(false);
        boardPasswordField.setVisible(false);
        editPasswordButton.setVisible(false);
        savePasswordButton.setVisible(false);

        viewBoardButton.setVisible(false);
        deleteBoardButton.setVisible(false);
    }

    public void switchSceneToHome() {
        server.disconnectWebsocket();
        mainCtrl.showHome();
    }
}
