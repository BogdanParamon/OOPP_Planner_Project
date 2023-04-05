package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Packet;
import commons.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static javafx.util.Duration.millis;

public class BoardOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    protected Map<Long, Map<Long, String>> accesses = new HashMap<>();
    private User user;
    @FXML
    private MFXListView<Pane> boards;

    @FXML
    private MFXTextField boardTitle;
    @FXML
    private Text subheading;
    @FXML
    private Text idErrorMsg;
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
        File accessesFile = new File("accesses.txt");
        if (accessesFile.exists()) {
            try {
                Scanner scanner = new Scanner(accessesFile);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(" ");
                    long userId = Long.parseLong(parts[0]);
                    long boardId = Long.parseLong(parts[1]);
                    String password = parts[2];
                    if (!accesses.containsKey(userId)) {
                        accesses.put(userId, new HashMap<>());
                        accesses.get(userId).put(boardId, password);
                    } else {
                        accesses.get(userId).put(boardId, password);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                accessesFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        mainCtrl.initHeader(root);
    }

    public void registerForNewBoards() {
        server.registerForMessages("/topic/boards/add/" + user.userId,
                Packet.class, boardIdAndTitleAndUserId -> {
                    Platform.runLater(() -> {
                        MFXButton button = new MFXButton(boardIdAndTitleAndUserId.stringValue);
                        button.setOnAction(event
                                -> switchSceneToBoard(server
                                .getBoardById(boardIdAndTitleAndUserId.longValue)));
                        Pane pane = new Pane();
                        pane.setPrefHeight(20);
                        pane.getChildren().add(button);
                        if (server.getBoardById(boardIdAndTitleAndUserId.longValue).getPassword() != null) {
                            ImageView lock = new ImageView("/client/images/lock-icon-11.png");
                            lock.setFitHeight(20);
                            lock.setFitWidth(20);
                            lock.setX(190);
                            pane.getChildren().add(lock);
                        }
                        boards.getItems().add(pane);
                        boardTitle.clear();
                    });
                });
        server.registerForMessages("/topic/boards/join/" + user.userId,
                Packet.class, boardIdAndTitleAndUserId -> {
                    Platform.runLater(() -> {
                        MFXButton button = new MFXButton(boardIdAndTitleAndUserId.stringValue);
                        button.setOnAction(event
                                -> switchSceneToBoard(server
                                .getBoardById(boardIdAndTitleAndUserId.longValue)));
                        Pane pane = new Pane();
                        pane.setPrefHeight(20);
                        pane.getChildren().add(button);
                        if (server.getBoardById(boardIdAndTitleAndUserId.longValue).getPassword() != null) {
                            ImageView lock = new ImageView("/client/images/lock-icon-11.png");
                            lock.setFitHeight(20);
                            lock.setFitWidth(20);
                            lock.setX(190);
                            pane.getChildren().add(lock);
                        }
                        boards.getItems().add(pane);
                        boardTitle.clear();
                    });
                });
    }

    public void load() {
        var boardTitlesAndIds = server.getBoardTitlesAndIdsByUserId(user.userId);
        boards.getItems().clear();
        boardTitlesAndIds.forEach((aLong, s) -> {
            MFXButton button = new MFXButton(s);
            button.setOnAction(event
                    -> switchSceneToBoard(server.getBoardById(aLong)));
            Pane pane = new Pane();
            pane.setPrefHeight(20);
            pane.getChildren().add(button);
            if (!knowsPassword(user, server.getBoardById(aLong))) {
                ImageView lock = new ImageView("/client/images/lock-icon-11.png");
                lock.setFitHeight(20);
                lock.setFitWidth(20);
                lock.setX(190);
                pane.getChildren().add(lock);
            }
            boards.getItems().add(pane);
            boardTitle.clear();
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

    public void showErrorMessage(Text errorMsg) {
        errorMsg.setVisible(true);

        Timeline timeline = new Timeline(
                new KeyFrame(millis(0), new KeyValue(errorMsg.translateXProperty(), 0)),
                new KeyFrame(millis(50), new KeyValue(errorMsg.translateXProperty(), -10)),
                new KeyFrame(millis(100), new KeyValue(errorMsg.translateXProperty(), 10)),
                new KeyFrame(millis(150), new KeyValue(errorMsg.translateXProperty(), -10)),
                new KeyFrame(millis(200), new KeyValue(errorMsg.translateXProperty(), 0))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void addBoard() {
        try {
            Board board = new Board(boardTitle.getText());
            server.send("/app/boards/add/" + user.userId, board);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    public void joinBoard() {
        try {
            Long boardId = Long.parseLong(boardTitle.getText());
            Board board = server.getBoardById(boardId);
            server.send("/app/boards/join/" + user.userId, board);
            switchSceneToBoard(board);
        } catch (NumberFormatException e) {
            showErrorMessage(idErrorMsg);
        }
    }


    public void switchSceneToHome() {
        server.disconnectWebsocket();
        mainCtrl.showHome();
    }

    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.boardCtrl.setUser(user);
        mainCtrl.boardCtrl.setUpProtection();
        mainCtrl.showBoard();
    }

    protected boolean knowsPassword(User user, Board board) {
        if (board.getPassword() == null)
            return true;
        if (accesses.containsKey(user.userId)) {
            if (accesses.get(user.userId).containsKey(board.boardId))
                return Objects.equals(accesses.get(user.userId).get(board.boardId), board.getPassword());
            return false;
        }
        return false;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
