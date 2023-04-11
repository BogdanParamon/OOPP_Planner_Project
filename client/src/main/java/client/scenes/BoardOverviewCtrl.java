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
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
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
    private final HashMap<Long, Pane> boardPaneMap = new HashMap<>();

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
                        long boardId = boardIdAndTitleAndUserId.longValue;
                        String boardName = boardIdAndTitleAndUserId.stringValue;

                        Pane pane = new Pane();
                        pane.setOnMouseClicked(event
                                -> switchSceneToBoard(server.getBoardById(boardId)));
                        pane.setPrefWidth(219);

                        Text boardTitleText = new Text(boardName);
                        boardTitleText.setLayoutX(25);
                        boardTitleText.setLayoutY(15);
                        boardTitleText.setFont(new Font("Roboto", 14));
                        pane.getChildren().add(boardTitleText);

                        MFXButton leaveButton = new MFXButton("");
                        ImageView leaveIcon = new ImageView("/client/images/leave_icon.png");
                        leaveIcon.setFitWidth(20);
                        leaveIcon.setFitHeight(20);
                        leaveButton.setGraphic(leaveIcon);
                        leaveButton.setOnAction(event -> leaveBoard(boardId));
                        leaveButton.setLayoutX(180);
                        leaveButton.setLayoutY(-3);
                        leaveButton.setPadding(new Insets(3, 3, 3, 3));
                        pane.getChildren().add(leaveButton);

                        MFXButton deleteButton = new MFXButton("");
                        ImageView deleteIcon = new ImageView("/client/images/delete_icon.png");
                        deleteIcon.setFitWidth(20);
                        deleteIcon.setFitHeight(20);
                        deleteButton.setGraphic(deleteIcon);
                        deleteButton.setOnAction(event -> deleteBoard(boardId));
                        deleteButton.setLayoutX(150);
                        deleteButton.setLayoutY(-3);
                        deleteButton.setPadding(new Insets(3, 3, 3, 3));
                        pane.getChildren().add(deleteButton);

                        if (!knowsPassword(user, server.getBoardById(boardId))) {
                            ImageView lock = new ImageView("/client/images/lock-icon-11.png");
                            lock.setFitHeight(20);
                            lock.setFitWidth(20);
                            pane.getChildren().add(lock);
                        }
                        boards.getItems().add(pane);
                        boardPaneMap.put(boardId, pane);
                        boardTitle.clear();
                    });
                });
    }

    public void registerForBoardLeaves() {
        server.registerForMessages("/topic/users/leave/" + user.userId, Long.class,
                boardId -> Platform.runLater(() -> {
                    boards.getItems().remove(boardPaneMap.get(boardId));
                    boardPaneMap.remove(boardId);
                }));
    }

    public void registerForBoardDeletes() {
        server.registerForBoardDeletes(boardId -> {
            Platform.runLater(() -> {
                boards.getItems().remove(boardPaneMap.get(boardId));
                boardPaneMap.remove(boardId);
            });
        });
    }

    public void load() {
        var boardTitlesAndIds = server.getBoardTitlesAndIdsByUserId(user.userId);
        boards.getItems().clear();
        boardTitlesAndIds.forEach((boardId, boardName) -> {
            Pane pane = new Pane();
            pane.setOnMouseClicked(event -> switchSceneToBoard(server.getBoardById(boardId)));
            pane.setPrefWidth(219);

            Text boardTitleText = new Text(boardName);
            boardTitleText.setLayoutX(25);
            boardTitleText.setLayoutY(15);
            boardTitleText.setFont(new Font("Roboto", 14));
            pane.getChildren().add(boardTitleText);

            MFXButton leaveButton = new MFXButton("");
            ImageView leaveIcon = new ImageView("/client/images/leave_icon.png");
            leaveIcon.setFitWidth(20);
            leaveIcon.setFitHeight(20);
            leaveButton.setGraphic(leaveIcon);
            leaveButton.setOnAction(event -> leaveBoard(boardId));
            leaveButton.setLayoutX(180);
            leaveButton.setLayoutY(-3);
            leaveButton.setPadding(new Insets(3, 3, 3, 3));
            pane.getChildren().add(leaveButton);

            MFXButton deleteButton = new MFXButton("");
            ImageView deleteIcon = new ImageView("/client/images/delete_icon.png");
            deleteIcon.setFitWidth(20);
            deleteIcon.setFitHeight(20);
            deleteButton.setGraphic(deleteIcon);
            deleteButton.setOnAction(event -> deleteBoard(boardId));
            deleteButton.setLayoutX(150);
            deleteButton.setLayoutY(-3);
            deleteButton.setPadding(new Insets(3, 3, 3, 3));
            pane.getChildren().add(deleteButton);

            if (!knowsPassword(user, server.getBoardById(boardId))) {
                ImageView lock = new ImageView("/client/images/lock-icon-11.png");
                lock.setFitHeight(20);
                lock.setFitWidth(20);
                pane.getChildren().add(lock);
                deleteButton.setDisable(true);
            }
            boards.getItems().add(pane);
            boardPaneMap.put(boardId, pane);
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

    public void leaveBoard(long boardId) {
        server.send("/app/users/leave/" + user.userId, boardId);
    }

    public void deleteBoard(long boardId) {
        server.deleteBoardById(boardId);
    }

    public void switchSceneToHome() {
        server.disconnectWebsocket();
        mainCtrl.showHome();
    }

    public void switchSceneToBoard(Board board) {
        mainCtrl.boardCtrl.setBoard(board);
        mainCtrl.boardCtrl.setUser(user);
        mainCtrl.boardCtrl.setAdminMode(false);
        mainCtrl.boardCtrl.setUpProtection();
        mainCtrl.showBoard();
    }

    protected boolean knowsPassword(User user, Board board) {
        if (board.getPassword() == null)
            return true;
        if (accesses.containsKey(user.userId)) {
            if (accesses.get(user.userId).containsKey(board.boardId))
                return Objects.equals(accesses.get(user.userId).get(board.boardId),
                        board.getPassword());
            return false;
        }
        return false;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void stop() {
        server.stop();
    }
}
