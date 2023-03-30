package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Packet;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.*;

public class BoardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Text boardName;
    @FXML
    private AnchorPane root;
    @FXML
    private HBox board_hbox;
    private Board board;
    @FXML
    private TextField newtTitle;
    @FXML
    private Button editTitle;
    @FXML
    private Button save;
    @FXML
    private Pane customize;
    @FXML
    private ColorPicker colorPickerBackground;
    @FXML
    private Text txtCust;
    @FXML
    private ColorPicker colorPickerBoard;
    @FXML
    private ScrollPane boardScrollPane;
    @FXML
    private ColorPicker colorPickerButtons;
    @FXML
    private VBox addListTaskVBox;
    @FXML
    private MFXButton addList;
    @FXML
    private MFXButton addTask;
    @FXML
    private Pane custimozePane;
    @FXML
    private Pane overviewBoardsPane;
    @FXML
    private MFXButton btnCustomize;
    @FXML
    private MFXButton btnOverviewBoards;

    private Set<StompSession.Subscription> subscriptions;

    @FXML
    private VBox tagList;

    @FXML
    private MFXButton addTag;

    /**
     * Setup server and main controller
     *
     * @param server   server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public BoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * @param url    The location used to resolve relative paths for the root object, or
     *               {@code null} if the location is not known.
     * @param bundle The resources used to localize the root object, or {@code null} if
     *               the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle bundle) {
        mainCtrl.initHeader(root);
    }

    public StompSession.Subscription registerForNewLists() {
        return server.registerForMessages("/topic/taskLists/add/" + board.boardId, TaskList.class,
                taskList -> Platform.runLater(() -> {
                    List listUI = new List(mainCtrl, server, taskList, this.board);
                    board_hbox.getChildren().add(listUI);
                }));
    }

    public StompSession.Subscription registerForListRenames() {
        return server.registerForMessages("/topic/taskLists/rename/" + board.boardId,
                Packet.class, listIdAndNewTitle -> Platform.runLater(() -> {
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        if (list.getTaskList().listId == listIdAndNewTitle.longValue) {
                            list.setTitle(listIdAndNewTitle.stringValue);
                            break;
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForBoardRenames() {
        return server.registerForMessages("/topic/boards/rename/" + board.boardId, Packet.class,
                boardIdAndNewTitle -> Platform.runLater(() -> {
                    boardName.setText(boardIdAndNewTitle.stringValue);
                }));
    }

    public StompSession.Subscription registerForNewTasks() {
        return server.registerForMessages("/topic/tasks/add/" + board.boardId, Packet.class,
                listIdAndTask -> Platform.runLater(() -> {
                    long listId = listIdAndTask.longValue;
                    Task task = listIdAndTask.task;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            taskList.tasks.add(0, task);
                            Card card = new Card(mainCtrl, server, task, taskList, board);
                            list.getList().getChildren().add(0, card);
                            break;
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForTaskUpdates() {
        return server.registerForMessages("/topic/tasks/update/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long listId = packet.longValue;
                    Task task = packet.task;
                    long taskId = task.taskId;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node cardNode : list.getList().getChildren()) {
                                Card card = (Card) cardNode;
                                if (card.getTask().taskId == taskId) {
                                    card.getTaskTitle().setText(task.title);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForListDeletes() {
        return server.registerForMessages("/topic/taskLists/delete/" + board.boardId, Long.class,
                listId -> Platform.runLater(() -> {
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        if (list.getTaskList().listId == listId) {
                            board_hbox.getChildren().remove(list);
                            board.lists.remove(list.getTaskList());
                            break;
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForTaskDeletes() {
        return server.registerForMessages("/topic/tasks/delete/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long listId = packet.longValue;
                    long taskId = packet.longValue2;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node cardNode : list.getList().getChildren()) {
                                Card card = (Card) cardNode;
                                if (card.getTask().taskId == taskId) {
                                    list.getList().getChildren().remove(card);
                                    list.getTaskList().tasks.remove(card.getTask());
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }));
    }

    public void switchToAddTask() {
        mainCtrl.showAddTask();
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchToBoardOverviewScene() {
        customize.setVisible(false);
        subscriptions.forEach(StompSession.Subscription::unsubscribe);
        mainCtrl.showBoardOverview();
    }

    public void setBoard(Board board) {
        this.board = board;
        boardName.setText(board.title);
        board_hbox.getChildren().clear();
        for (var taskList : board.lists) {
            List list = new List(mainCtrl, server, taskList, this.board);
            board_hbox.getChildren().add(list);
        }

        tagList.getChildren().remove(1, tagList.getChildren().size());
        for (var tag : board.tags) {
            Tag tagUI = new Tag(mainCtrl, server, tag);
            tagList.getChildren().add(tagUI);
        }

        root.setStyle("-fx-background-color: #" + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #" + board.backgroundColor + ";");
        save.setStyle("-fx-background-color: #" + board.backgroundColor + ";");
        addListTaskVBox.setStyle("-fx-background-color: #"
                + board.buttonsBackground + "; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        addTask.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        btnCustomize.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        btnOverviewBoards.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        overviewBoardsPane.setStyle("-fx-background-color: #"
                + board.buttonsBackground + ";-fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: #"
                + board.buttonsBackground + ";-fx-background-radius: 10px;");

        subscriptions = new HashSet<>();
        subscriptions.add(registerForNewLists());
        subscriptions.add(registerForNewTasks());
        subscriptions.add(registerForListDeletes());
        subscriptions.add(registerForTaskDeletes());
        subscriptions.add(registerForBoardRenames());
        subscriptions.add(registerForListRenames());
        subscriptions.add(registerForTaskUpdates());
    }

    public void addList() {
        TaskList list = new TaskList("New List");
        server.send("/app/taskLists/add/" + board.boardId, list);
    }

    public void updateTitle() {
        newtTitle.setVisible(true);
        boardName.setVisible(false);
        editTitle.setVisible(false);
        save.setVisible(true);
    }

    public void saveNewTitle() {
        String newTitleS = newtTitle.getText().trim();
        newtTitle.setVisible(false);
        boardName.setVisible(true);
        editTitle.setVisible(true);
        save.setVisible(false);
        if (!newTitleS.isEmpty()) {
            renameBoard(newTitleS);
        }
        newtTitle.setText("");
    }

    public void renameBoard(String newTitle) {
        try {
            server.send("/app/boards/rename/" + board.boardId, newTitle);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void updateBoard(Board board) {
        try {
            server.send("/app/boards/update", board);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void addTag() {
        String color = String.format("#%06X",
                new Random(System.currentTimeMillis()).nextInt(0x1000000));
        commons.Tag tag = new commons.Tag("New Tag", color);
        tag = server.addTagToBoard(board.boardId, tag);
        tagList.getChildren().add(1, new Tag(mainCtrl, server, tag));
    }

    public void showCustomize() {
        if (customize.isVisible()) customize.setVisible(false);
        else customize.setVisible(true);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        colorPickerButtons.setValue(Color.valueOf(board.buttonsBackground));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void closeCustomize() {
        customize.setVisible(false);
    }

    public void applyChanges() {
        //background color
        String rootColor = colorPickerBackground.getValue().toString().substring(2, 8);
        root.setStyle("-fx-background-color: #" + rootColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #" + rootColor + ";");
        save.setStyle("-fx-background-color: #" + rootColor + ";");
        this.board.backgroundColor = rootColor;
        txtCust.setFill(Paint.valueOf(rootColor));
        //board color
        String boardColor = colorPickerBoard.getValue().toString().substring(2, 8);
        boardScrollPane.setStyle("-fx-background-color: #"
                + boardColor + "; -fx-background-radius: 10px;");
        //button color
        String buttonColor = colorPickerButtons.getValue().toString().substring(2, 8);
        addListTaskVBox.setStyle("-fx-background-color: #"
                + buttonColor + "; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: #" + buttonColor + ";");
        addTask.setStyle("-fx-background-color: #" + buttonColor + ";");
        btnCustomize.setStyle("-fx-background-color: #" + buttonColor + ";");
        btnOverviewBoards.setStyle("-fx-background-color: #" + buttonColor + ";");
        overviewBoardsPane.setStyle("-fx-background-color: #"
                + buttonColor + ";-fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: #"
                + buttonColor + ";-fx-background-radius: 10px;");
        this.board.buttonsBackground = buttonColor;

        updateBoard(board);
    }

    public void resetBackgroundColor() {
        this.board.backgroundColor = "ffffff";
        root.setStyle("-fx-background-color: #" + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #ffffff;");
        save.setStyle("-fx-background-color: #ffffff;");
        updateBoard(board);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void resetBoardColor() {
        boardScrollPane.setStyle("-fx-background-color: #ddd; -fx-background-radius: 10px;");

        colorPickerBoard.setValue(Color.valueOf("ddd"));
    }

    public void resetButtonColor() {
        this.board.buttonsBackground = "ddd";
        addListTaskVBox.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: ddd;");
        addTask.setStyle("-fx-background-color: ddd;");
        btnOverviewBoards.setStyle("-fx-background-color: ddd;");
        btnCustomize.setStyle("-fx-background-color: ddd;");
        overviewBoardsPane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");

        updateBoard(board);
        colorPickerButtons.setValue(Color.valueOf("ddd"));
    }

    public AnchorPane getRoot() {
        return root;
    }
}
