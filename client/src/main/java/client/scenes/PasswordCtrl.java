package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.User;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.util.Duration.millis;

public class PasswordCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Board board;
    private User user;

    private boolean mode; //0 -> set, 1 -> ask

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

    public void setUp() {
        if (!mode)
            setText("Set password for board " + board.title);
        else setText("Please enter password for board " + board.title);
    }


    public void setText(String text) {
        this.text.setText(text);
    }

    public void ok() {
        if (mode) {
            if (Objects.equals(passwordField.getText(), board.getPassword())) {
                mainCtrl.boardCtrl.enable();
                try {
                    FileWriter fw = new FileWriter(new File("accesses.txt"), true);
                    BufferedWriter bf = new BufferedWriter(fw);
                    PrintWriter writer = new PrintWriter(bf);
                    writer.write(user.userId + " " + board.boardId + " " + board.getPassword() + '\n');
                    writer.flush();
                    if (!mainCtrl.boardOverviewCtrl.accesses.containsKey(user.userId)) {
                        mainCtrl.boardOverviewCtrl.accesses.put(user.userId, new HashMap<>());
                        mainCtrl.boardOverviewCtrl.accesses.get(user.userId).put(board.boardId, board.getPassword());
                    } else
                        mainCtrl.boardOverviewCtrl.accesses.get(user.userId).put(board.boardId, board.getPassword());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mainCtrl.boardCtrl.setUpProtection();
                switchSceneToBoard(board);
                setUp();
                passwordField.clear();
                errorMsg.setVisible(false);
            } else {
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
        } else {
            board.setPassword(passwordField.getText());
            System.out.println(board);
            server.send("/app/boards/update", board);
            mainCtrl.boardCtrl.enable();
            try {
                FileWriter fw = new FileWriter(new File("accesses.txt"), true);
                BufferedWriter bf = new BufferedWriter(fw);
                PrintWriter writer = new PrintWriter(bf);
                writer.write(user.userId + " " + board.boardId + " " + board.getPassword() + '\n');
                writer.flush();
                if (!mainCtrl.boardOverviewCtrl.accesses.containsKey(user.userId)) {
                    mainCtrl.boardOverviewCtrl.accesses.put(user.userId, new HashMap<>());
                    mainCtrl.boardOverviewCtrl.accesses.get(user.userId).put(board.boardId, board.getPassword());
                } else
                    mainCtrl.boardOverviewCtrl.accesses.get(user.userId).put(board.boardId, board.getPassword());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainCtrl.boardCtrl.setUpProtection();
            switchSceneToBoard(board);
            passwordField.clear();
        }
    }

    public void goBack() {
        mainCtrl.showBoard();
        passwordField.clear();
    }

    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.showBoard();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }
}
