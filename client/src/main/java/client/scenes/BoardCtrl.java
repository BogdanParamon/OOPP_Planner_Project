package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Packet;
import commons.Subtask;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
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
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

public class BoardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final String fxBackgroundColor = "-fx-background-color: #";
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
    @FXML
    private Text logo;
    @FXML
    private ColorPicker colorPickerBackgroundFont;
    @FXML
    private ColorPicker colorPickerButtonsFont;
    @FXML
    private MFXScrollPane tagsPane;
    @FXML
    private Text txtTags;

    private Set<StompSession.Subscription> subscriptions;

    @FXML
    private VBox tagList;

    @FXML
    private MFXButton addTag;

    private Pane blurPane;

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
                                    card.getDetailedTask().getDtvDescription()
                                            .setText(task.description);
                                    card.getDetailedTask().updateSubtaskOrder(task);
                                    if (!task.description.trim().equals(""))
                                        card.showDescriptionImage();
                                    else card.hideDescriptionImage();
                                    card.getDetailedTask().getDtvTitle().setText(task.title);
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
                            for (Node node1 : list.getList().getChildren()) {
                                if (!(node1 instanceof Card)) continue;
                                Card card = (Card) node1;
                                if (card.isHasDetailedTaskOpen())
                                    card.getDetailedTask().stopDisplayingDialog();
                            }
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
                                if (!(cardNode instanceof Card)) continue;
                                Card card = (Card) cardNode;
                                if (card.getTask().taskId == taskId) {
                                    if (card.isHasDetailedTaskOpen())
                                        card.getDetailedTask().stopDisplayingDialog();
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

    public StompSession.Subscription registerForNewSubtasks() {
        return server.registerForMessages("/topic/subtasks/add/" + board.boardId, Packet.class,
                taskIdlistIdAndSubtask -> Platform.runLater(() -> {
                    long taskId = taskIdlistIdAndSubtask.longValue;
                    long listId = taskIdlistIdAndSubtask.longValue2;
                    Subtask subtask = taskIdlistIdAndSubtask.subtask;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node node1 : list.getList().getChildren()) {
                                Card card = (Card) node1;
                                Task task = card.getTask();
                                if (task.taskId == taskId) {
                                    task.subtasks.add(0, subtask);
                                    client.scenes.Subtask UISubtask =
                                            new client.scenes.Subtask(mainCtrl,
                                                    server, board, taskList, task, subtask);
                                    card.getDetailedTask().
                                            getTasks_vbox().getChildren().add(0, UISubtask);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForSubtaskRename() {
        return server.registerForMessages("/topic/subtasks/rename/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue2;
                    Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node node1 : list.getList().getChildren()) {
                                Card card = (Card) node1;
                                Task task = card.getTask();
                                if (task.taskId == taskId) {
                                    for (Node node2 : card.getDetailedTask()
                                            .getTasks_vbox().getChildren()) {
                                        client.scenes.Subtask subtaskUI =
                                                (client.scenes.Subtask) node2;
                                        Subtask subtaskDB = subtaskUI.getSubtask();
                                        if (subtaskDB.subTaskId == subtask.subTaskId) {
                                            subtaskUI.getCheckbox().setText(subtask.subtaskText);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForDragNDrops() {
        return server.registerForMessages("/topic/tasks/drag/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long taskId = packet.longValue;
                    long dragFromListId = packet.longValue2;
                    long dragToListId = packet.longValue3;
                    int dragToIndex = packet.intValue;
                    List fromList = null;
                    List toList = null;
                    Card draggedCard = null;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == dragFromListId) {
                            fromList = list;
                            for (Node cardNode : list.getList().getChildren()) {
                                Card card = (Card) cardNode;
                                if (card.getTask().taskId == taskId) {
                                    draggedCard = card;
                                    break;
                                }
                            }
                        }
                        if (taskList.listId == dragToListId) {
                            toList = list;
                        }
                    }
                    if (fromList != null && toList != null && draggedCard != null) {
                        fromList.getList().getChildren().remove(draggedCard);
                        fromList.getTaskList().tasks.remove(draggedCard.getTask());
                        toList.getList().getChildren().add(dragToIndex, draggedCard);
                        toList.getTaskList().tasks.add(draggedCard.getTask());
                        draggedCard.setTaskList(toList.getTaskList());
                    }
                }));
    }

    public StompSession.Subscription registerForSubtaskDelete() {
        return server.registerForMessages("/topic/subtasks/delete/" + board.boardId, Packet.class,
                taskIdlistIdAndSubtaskId -> Platform.runLater(() -> {
                    long listId = taskIdlistIdAndSubtaskId.longValue;
                    long taskId = taskIdlistIdAndSubtaskId.longValue2;
                    long subtaskId = taskIdlistIdAndSubtaskId.longValue3;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node node1 : list.getList().getChildren()) {
                                Card card = (Card) node1;
                                Task task = card.getTask();
                                if (task.taskId == taskId) {
                                    for (Node node2 : card.getDetailedTask()
                                            .getTasks_vbox().getChildren()) {
                                        client.scenes.Subtask subtaskUI =
                                                (client.scenes.Subtask) node2;
                                        Subtask subtaskDB = subtaskUI.getSubtask();
                                        if (subtaskDB.subTaskId == subtaskId) {
                                            card.getDetailedTask().getTasks_vbox()
                                                    .getChildren().remove(subtaskUI);
                                            task.subtasks.remove(subtaskDB);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForSubtaskStatus() {
        return server.registerForMessages("/topic/subtasks/status/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue2;
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        TaskList taskList = list.getTaskList();
                        if (taskList.listId == listId) {
                            for (Node node1 : list.getList().getChildren()) {
                                Card card = (Card) node1;
                                Task task = card.getTask();
                                if (task.taskId == taskId) {
                                    for (Node node2 : card.getDetailedTask()
                                            .getTasks_vbox().getChildren()) {
                                        client.scenes.Subtask subtaskUI =
                                                (client.scenes.Subtask) node2;
                                        if (subtask.subTaskId == subtaskUI
                                                .getSubtask().subTaskId) {
                                            subtaskUI.getCheckbox()
                                                    .setSelected(subtask.subtaskBoolean);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }));
    }

    public StompSession.Subscription registerForNewTags() {
        return server.registerForMessages("/topic/boards/addTag/" + board.boardId,
                commons.Tag.class,
                tag -> Platform.runLater(() -> {
                    board.tags.add(tag);
                    Tag tagUI = new Tag(mainCtrl, server, tag, board);
                    tagList.getChildren().add(1, tagUI);
                }));
    }

    public StompSession.Subscription registerForTagUpdates() {
        return server.registerForMessages("/topic/boards/updateTag/" + board.boardId,
                commons.Tag.class,
                tag -> Platform.runLater(() -> {
                    System.out.println("tag updated: " + tag);
                    board.tags.removeIf(t -> t.tagId == tag.tagId);
                    board.tags.add(tag);
                    int index = 0;
                    for (int i = 0; i < tagList.getChildren().size(); i++) {
                        if (tagList.getChildren().get(i) instanceof Tag) {
                            Tag tagUI = (Tag) tagList.getChildren().get(i);
                            if (tagUI.tag.tagId == tag.tagId) {
                                System.out.println(i);
                                tagList.getChildren().set(i, new Tag(mainCtrl, server, tag, board));
                                break;
                            }
                        }
                    }

                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        for (Node cardNode : list.getList().getChildren()) {
                            if (!(cardNode instanceof Card)) continue;
                            Card card = (Card) cardNode;
                            card.updateTag(tag);
                        }
                    }


                }));
    }

    public StompSession.Subscription registerForTagDeletes() {
        return server.registerForMessages("/topic/boards/deleteTag/" + board.boardId, Long.class,
                tagId -> Platform.runLater(() -> {
                    board.tags.removeIf(t -> t.tagId == tagId);
                    for (int i = 0; i < tagList.getChildren().size(); i++) {
                        if (tagList.getChildren().get(i) instanceof Tag) {
                            Tag tagUI = (Tag) tagList.getChildren().get(i);
                            if (tagUI.tag.tagId == tagId) {
                                tagList.getChildren().remove(i);
                                break;
                            }
                        }
                    }

                    for (var list : board.lists) {
                        for (var task : list.tasks) {
                            task.tags.removeIf(t -> t.tagId == tagId);
                        }
                    }

                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        for (Node cardNode : list.getList().getChildren()) {
                            if (!(cardNode instanceof Card)) continue;
                            Card card = (Card) cardNode;
                            card.removeTag(tagId);
                        }
                    }
                }));
    }


    public void switchToAddTask() {
        mainCtrl.showAddTask();
    }

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
            Tag tagUI = new Tag(mainCtrl, server, tag, board);
            tagList.getChildren().add(tagUI);
        }

        setBoardColors(board);
        setBoardFontColors(board);

        subscriptions = new HashSet<>();
        subscriptions.add(registerForNewLists());
        subscriptions.add(registerForNewTasks());
        subscriptions.add(registerForListDeletes());
        subscriptions.add(registerForTaskDeletes());
        subscriptions.add(registerForBoardRenames());
        subscriptions.add(registerForListRenames());
        subscriptions.add(registerForTaskUpdates());
        subscriptions.add(registerForNewSubtasks());
        subscriptions.add(registerForSubtaskRename());
        subscriptions.add(registerForSubtaskDelete());
        subscriptions.add(registerForSubtaskStatus());
        subscriptions.add(registerForDragNDrops());
        subscriptions.add(registerForNewTags());
        subscriptions.add(registerForTagUpdates());
        subscriptions.add(registerForTagDeletes());
    }

    private void setBoardColors(Board board) {
        root.setStyle(fxBackgroundColor + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        save.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        addListTaskVBox.setStyle(fxBackgroundColor
                + board.buttonsBackground + "; -fx-background-radius: 10px;");
        addList.setStyle(fxBackgroundColor + board.buttonsBackground + ";");
        addTask.setStyle(fxBackgroundColor + board.buttonsBackground + ";");
        btnCustomize.setStyle(fxBackgroundColor + board.buttonsBackground + ";");
        btnOverviewBoards.setStyle(fxBackgroundColor + board.buttonsBackground + ";");
        overviewBoardsPane.setStyle(fxBackgroundColor
                + board.buttonsBackground + ";-fx-background-radius: 10px;");
        custimozePane.setStyle(fxBackgroundColor
                + board.buttonsBackground + ";-fx-background-radius: 10px;");
        tagsPane.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        addTag.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        //board
        boardScrollPane.setStyle(fxBackgroundColor
                + board.boardColor + "; -fx-background-radius: 5px");
        board_hbox.setStyle(fxBackgroundColor
                + board.boardColor + "; -fx-background-radius: 1px");
    }

    private void setBoardFontColors(Board board) {
        logo.setFill(Paint.valueOf(board.backgroundColorFont));
        boardName.setFill(Paint.valueOf(board.backgroundColorFont));
        btnCustomize.setTextFill(Paint.valueOf(board.buttonsColorFont));
        btnOverviewBoards.setTextFill(Paint.valueOf(board.buttonsColorFont));
        addList.setTextFill(Paint.valueOf(board.buttonsColorFont));
        addTask.setTextFill(Paint.valueOf(board.buttonsColorFont));
        txtTags.setFill(Paint.valueOf(board.backgroundColorFont));
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
        server.send("/app/boards/addTag/" + board.boardId, tag);
    }

    public void showCustomize() {
        if (customize.isVisible()) customize.setVisible(false);
        else customize.setVisible(true);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        colorPickerButtons.setValue(Color.valueOf(board.buttonsBackground));
        colorPickerBackgroundFont.setValue(Color.valueOf(board.backgroundColorFont));
        colorPickerButtonsFont.setValue(Color.valueOf(board.buttonsColorFont));
        colorPickerBoard.setValue(Color.valueOf(board.boardColor));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void closeCustomize() {
        customize.setVisible(false);
    }

    public void applyChanges() {
        //background color
        String rootColor = colorPickerBackground.getValue().toString().substring(2, 8);
        root.setStyle(fxBackgroundColor + rootColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle(fxBackgroundColor + rootColor
                + "; -fx-border-color: #" + rootColor + ";");
        save.setStyle(fxBackgroundColor + rootColor + ";");
        this.board.backgroundColor = rootColor;
        tagsPane.setStyle(fxBackgroundColor + rootColor + ";");
        addTag.setStyle(fxBackgroundColor + rootColor + ";");
        txtCust.setFill(Paint.valueOf(rootColor));
        //board color
        String boardColor = colorPickerBoard.getValue()
                .toString().substring(2, 8);
        boardScrollPane.setStyle(fxBackgroundColor
                + boardColor + "; -fx-background-radius: 5px");
        board_hbox.setStyle(fxBackgroundColor
                + boardColor + "; -fx-background-radius: 1px");
        this.board.boardColor = boardColor;
        //button color
        String buttonColor = colorPickerButtons.getValue().toString().substring(2, 8);
        addListTaskVBox.setStyle(fxBackgroundColor
                + buttonColor + "; -fx-background-radius: 10px;");
        addList.setStyle(fxBackgroundColor + buttonColor + ";");
        addTask.setStyle(fxBackgroundColor + buttonColor + ";");
        btnCustomize.setStyle(fxBackgroundColor + buttonColor + ";");
        btnOverviewBoards.setStyle(fxBackgroundColor + buttonColor + ";");
        overviewBoardsPane.setStyle(fxBackgroundColor
                + buttonColor + ";-fx-background-radius: 10px;");
        custimozePane.setStyle(fxBackgroundColor
                + buttonColor + ";-fx-background-radius: 10px;");
        this.board.buttonsBackground = buttonColor;

        applyChangesFont();

        updateBoard(board);
    }

    public void applyChangesFont() {
        //background font
        String backgroundFontColor = colorPickerBackgroundFont
                .getValue().toString().substring(2, 8);
        logo.setFill(Paint.valueOf(backgroundFontColor));
        boardName.setFill(Paint.valueOf(backgroundFontColor));
        this.board.backgroundColorFont = backgroundFontColor;
        txtTags.setFill(Paint.valueOf(backgroundFontColor));
        //buttons font
        String buttonsFontColor = colorPickerButtonsFont.getValue().toString().substring(2, 8);
        btnCustomize.setTextFill(Paint.valueOf(buttonsFontColor));
        btnOverviewBoards.setTextFill(Paint.valueOf(buttonsFontColor));
        addList.setTextFill(Paint.valueOf(buttonsFontColor));
        addTask.setTextFill(Paint.valueOf(buttonsFontColor));
        this.board.buttonsColorFont = buttonsFontColor;
    }

    public void resetBackgroundColor() {
        this.board.backgroundColor = "ffffff";
        root.setStyle(fxBackgroundColor + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #ffffff;");
        save.setStyle("-fx-background-color: #ffffff;");
        tagsPane.setStyle("-fx-background-color: white;");
        addTag.setStyle("-fx-background-color: white;");
        updateBoard(board);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void resetBoardColor() {
        boardScrollPane.setStyle("-fx-background-color: #ddd; -fx-background-radius: 5px");
        board_hbox.setStyle("-fx-background-color: #ddd; -fx-background-radius: 1px");
        this.board.boardColor = "ddd";
        updateBoard(board);

        colorPickerBoard.setValue(Color.valueOf("ddd"));
    }

    public void resetButtonColor() {
        this.board.buttonsBackground = "ddd";
        addListTaskVBox.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        String fxBckgroundColorDDD = "-fx-background-color: ddd;";
        addList.setStyle(fxBckgroundColorDDD);
        addTask.setStyle(fxBckgroundColorDDD);
        btnOverviewBoards.setStyle(fxBckgroundColorDDD);
        btnCustomize.setStyle(fxBckgroundColorDDD);
        overviewBoardsPane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");

        updateBoard(board);
        colorPickerButtons.setValue(Color.valueOf("ddd"));
    }

    public void resetBackgroundFont() {
        logo.setFill(Paint.valueOf("Black"));
        boardName.setFill(Paint.valueOf("Black"));
        txtTags.setFill(Paint.valueOf("Black"));
        board.backgroundColorFont = "Black";

        updateBoard(board);
        colorPickerBackgroundFont.setValue(Color.valueOf("Black"));
    }

    public void resetButtonFont() {
        btnOverviewBoards.setTextFill(Paint.valueOf("Black"));
        btnCustomize.setTextFill(Paint.valueOf("Black"));
        addList.setTextFill(Paint.valueOf("Black"));
        addTask.setTextFill(Paint.valueOf("Black"));
        board.buttonsColorFont = "Black";

        updateBoard(board);
        colorPickerButtonsFont.setValue(Color.valueOf("Black"));
    }

    public void resetAllColors() {
        resetBoardColor();
        resetBackgroundColor();
        resetButtonColor();
        resetBackgroundFont();
        resetButtonFont();
    }

    public AnchorPane getRoot() {
        return root;
    }

    public void displayDetailedTask(DetailedTask detailedTask) {
        blurPane = new Pane();
        blurPane.setPrefSize(900, 600);
        blurPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        root.getChildren().add(blurPane);
        detailedTask.setStyle("-fx-background-radius: 20");
        detailedTask.setLayoutX(150);
        detailedTask.setLayoutY(100);
        blurPane.setOnMouseClicked(event -> {
            root.getChildren().remove(blurPane);
            root.getChildren().remove(detailedTask);
        });
        root.getChildren().add(detailedTask);
    }

    public void stopDisplayingDialog(DetailedTask detailedTask) {
        root.getChildren().remove(blurPane);
        root.getChildren().remove(detailedTask);
    }
}
