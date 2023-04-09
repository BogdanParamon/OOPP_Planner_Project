package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
    private final String fxBackgroundColor = "-fx-background-color: #";
    @FXML
    private Text boardName;
    @FXML
    private AnchorPane root;
    @FXML
    private HBox board_hbox;
    private Board board;
    private User user;
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
    @FXML
    private ImageView lock;
    @FXML
    private ColorPicker colorPickerListsColor;
    @FXML
    private ColorPicker colorPickerListsFont;
    @FXML
    private HBox tags_hbox;
    @FXML
    private ColorPicker presetB1;
    @FXML
    private ColorPicker presetB2;
    @FXML
    private ColorPicker presetB3;
    @FXML
    private ColorPicker presetF1;
    @FXML
    private ColorPicker presetF2;
    @FXML
    private ColorPicker presetF3;
    @FXML
    private Text pointer1;
    @FXML
    private Text pointer2;
    @FXML
    private Text pointer3;
    private Set<StompSession.Subscription> subscriptions;

    @FXML
    private VBox tagList;

    @FXML
    private MFXButton addTag;
    private boolean adminMode;
    private HashMap<Long, List> listMap;
    private HashMap<Long, Tag> tagMap;

    @FXML private Pane blurPane;

    @FXML
    private MFXButton passwordButton;

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

    public void setUpProtection() {
        if (!mainCtrl.boardOverviewCtrl.knowsPassword(user, board)) {
            disable();
            passwordButton.setText("Unlock");
            passwordButton.setOnMouseClicked(event -> {
                askForPassword(board, user);
            });
            lock.setOnMouseClicked(event -> {
                askForPassword(board, user);
            });
        } else {
            enable();
            passwordButton.setText("Lock");
            passwordButton.setOnMouseClicked(event -> {
                setPassword(board, user);
            });
            lock.setOnMouseClicked(event -> {
                setPassword(board, user);
            });
        }
    }

    protected void askForPassword(Board board, User user) {
        mainCtrl.passwordCtrl.setBoard(board);
        mainCtrl.passwordCtrl.setUser(user);
        mainCtrl.passwordCtrl.setMode(true);
        mainCtrl.passwordCtrl.setUp();
        mainCtrl.showPassword();
    }

    protected void setPassword(Board board, User user) {
        mainCtrl.passwordCtrl.setBoard(board);
        mainCtrl.passwordCtrl.setUser(user);
        mainCtrl.passwordCtrl.setMode(false);
        mainCtrl.passwordCtrl.setUp();
        mainCtrl.showPassword();
    }

    public void handleShortcuts(KeyEvent event) {
        if (Card.focused == null) return;

        if (event.isShiftDown()) {
            if (event.getCode() == KeyCode.UP) {
                Card.focused.simulateDragAndDrop(Card.Direction.UP);
            } else if (event.getCode() == KeyCode.DOWN) {
                Card.focused.simulateDragAndDrop(Card.Direction.DOWN);
            }
        } else if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
            Card.focused.deleteTask();
        } else if (event.getCode() == KeyCode.ENTER) {
            if (!Card.focused.isDetailedTaskOpen) {
                Card.focused.displayDialog();
            }
        } else if (event.getCode() == KeyCode.ESCAPE) {
            if (Card.focused.isDetailedTaskOpen) {
                Card.focused.closeDialog();
            }
        } else if (event.getCode() == KeyCode.E) {
            Card.focused.editTaskTitle();
        } else if (event.getCode() == KeyCode.C) {
            showCustomize();
        } else if (event.getCode() == KeyCode.T) {
            Card.focused.showAddTagPopup();
        }
    }



    public StompSession.Subscription registerForNewLists() {
        return server.registerForMessages("/topic/taskLists/add/" + board.boardId, TaskList.class,
                taskList -> Platform.runLater(() -> {
                    List listUI = new List(mainCtrl, server, taskList, this.board);
                    listUI.getScrollPane().setStyle(fxBackgroundColor
                            + board.listsColor + "; -fx-background-radius: 10px;");
                    listUI.getAddButton().setStyle(fxBackgroundColor + board.listsColor + ";");
                    listUI.getTitle().setStyle(fxBackgroundColor + board.listsColor
                            + "; -fx-border-radius: 10px; -fx-background-radius: 10px;" +
                            " -fx-border-color: transparent;");
                    listUI.getTitle().setTextFill(Color.valueOf(board.listsFontColor));
                    listUI.getDeleteTaskListButton().
                            setStyle(fxBackgroundColor + board.listsColor + ";");
                    board_hbox.getChildren().add(listUI);
                    board.lists.add(taskList);
                    listMap.put(taskList.listId, listUI);
                }));
    }

    public StompSession.Subscription registerForListRenames() {
        return server.registerForMessages("/topic/taskLists/rename/" + board.boardId,
                Packet.class, listIdAndNewTitle -> Platform.runLater(() -> {
                    listMap.get(listIdAndNewTitle.longValue)
                            .setTitle(listIdAndNewTitle.stringValue);
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
                    List list = listMap.get(listId);
                    list.getTaskList().tasks.add(0, task);
                    Card card = new Card(mainCtrl, server, task, list.getTaskList(), board);
                    if (board.currentPreset == 0) {
                        loadCardColors(card, board.cardsBackground1, board.cardsFont1);
                    } else if (board.currentPreset == 1) {
                        loadCardColors(card, board.cardsBackground2, board.cardsFont2);
                    } else {
                        loadCardColors(card, board.cardsBackground3, board.cardsFont3);
                    }
                    list.getList().getChildren().add(0, card);
                    list.getCardMap().put(task.taskId, card);
                }));
    }

    public StompSession.Subscription registerForTaskUpdates() {
        return server.registerForMessages("/topic/tasks/update/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long listId = packet.longValue;
                    Task task = packet.task;
                    long taskId = task.taskId;

                    Card card = listMap.get(listId).getCardMap().get(taskId);

                    card.getTaskTitle().setText(task.title);
                    card.getDetailedTask().getDtvDescription()
                            .setText(task.description);
                    card.getDetailedTask().updateDetails();
                    if (!task.description.trim().equals(""))
                        card.showDescriptionImage();
                    else card.hideDescriptionImage();
                    card.getDetailedTask().getDtvTitle().setText(task.title);
                }));
    }

    public StompSession.Subscription registerForListDeletes() {
        return server.registerForMessages("/topic/taskLists/delete/" + board.boardId, Long.class,
                listId -> Platform.runLater(() -> {
                    List list = listMap.get(listId);
                    list.getCardMap().forEach((id, card) -> {
                        if (card.isHasDetailedTaskOpen())
                            card.getDetailedTask().stopDisplayingDialog();
                    });
                    board_hbox.getChildren().remove(list);
                    board.lists.remove(list.getTaskList());
                    listMap.remove(listId);
                }));
    }

    public StompSession.Subscription registerForTaskDeletes() {
        return server.registerForMessages("/topic/tasks/delete/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long listId = packet.longValue;
                    long taskId = packet.longValue2;
                    List list = listMap.get(listId);
                    Card card = list.getCardMap().get(taskId);

                    if (card.isHasDetailedTaskOpen())
                        card.getDetailedTask().stopDisplayingDialog();
                    list.getList().getChildren().remove(card);
                    list.getTaskList().tasks.remove(card.getTask());
                    list.getCardMap().remove(taskId);
                }));
    }

    public StompSession.Subscription registerForNewSubtasks() {
        return server.registerForMessages("/topic/subtasks/add/" + board.boardId, Packet.class,
                taskIdlistIdAndSubtask -> Platform.runLater(() -> {
                    long taskId = taskIdlistIdAndSubtask.longValue;
                    long listId = taskIdlistIdAndSubtask.longValue2;


                    commons.Subtask subtask = taskIdlistIdAndSubtask.subtask;
                    List list = listMap.get(listId);
                    Card card = list.getCardMap().get(taskId);
                    Task task = card.getTask();

                    task.subtasks.add(0, subtask);
                    client.scenes.Subtask UISubtask = new client.scenes.Subtask(mainCtrl, server,
                            board, list.getTaskList(), task, subtask);
                    UISubtask.getCheckbox().setSelected(subtask.subtaskBoolean);
                    card.getDetailedTask().getTasks_vbox().getChildren().add(0, UISubtask);
                    card.getDetailedTask().getSubtaskMap().put(subtask.subTaskId, UISubtask);
                }));
    }

    public StompSession.Subscription registerForSubtaskRename() {
        return server.registerForMessages("/topic/subtasks/rename/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue2;
                    commons.Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;

                    listMap.get(listId)
                            .getCardMap().get(taskId)
                            .getDetailedTask().getSubtaskMap().get(subtask.subTaskId)
                            .getCheckbox().setText(subtask.subtaskText);
                }));
    }

    public StompSession.Subscription registerForDragNDrops() {
        return server.registerForMessages("/topic/tasks/drag/" + board.boardId, Packet.class,
                packet -> Platform.runLater(() -> {
                    long taskId = packet.longValue;
                    long dragFromListId = packet.longValue2;
                    long dragToListId = packet.longValue3;
                    int dragToIndex = packet.intValue;
                    List fromList = listMap.get(dragFromListId);
                    List toList = listMap.get(dragToListId);
                    Card draggedCard = fromList.getCardMap().get(taskId);

                    if (fromList != null && toList != null && draggedCard != null) {
                        fromList.getList().getChildren().remove(draggedCard);
                        fromList.getTaskList().tasks.remove(draggedCard.getTask());
                        fromList.getCardMap().remove(taskId);

                        toList.getList().getChildren().add(dragToIndex, draggedCard);
                        toList.getTaskList().tasks.add(draggedCard.getTask());
                        toList.getCardMap().put(taskId, draggedCard);
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
                    Card card = listMap.get(listId).getCardMap().get(taskId);
                    Subtask subtaskUI = card.getDetailedTask().getSubtaskMap().get(subtaskId);

                    card.getDetailedTask().getTasks_vbox().getChildren().remove(subtaskUI);
                    card.getTask().subtasks.remove(subtaskUI.getSubtask());
                }));
    }

    public StompSession.Subscription registerForSubtaskStatus() {
        return server.registerForMessages("/topic/subtasks/status/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    commons.Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue2;

                    listMap.get(listId)
                            .getCardMap().get(taskId)
                            .getDetailedTask().getSubtaskMap().get(subtask.subTaskId)
                            .getCheckbox().setSelected(subtask.subtaskBoolean);
                    listMap.get(listId)
                            .getCardMap().get(taskId).updateProgress();
                }));
    }

    public StompSession.Subscription registerForNewTags() {
        return server.registerForMessages("/topic/boards/addTag/" + board.boardId,
                commons.Tag.class,
                tag -> Platform.runLater(() -> {
                    board.tags.add(tag);
                    Tag tagUI = new Tag(mainCtrl, server, tag, board);
                    tagUI.deleteTag.setStyle(fxBackgroundColor + board.backgroundColor);
                    tagUI.saveTag.setStyle(fxBackgroundColor + board.backgroundColor);
                    tagUI.edit.setStyle(fxBackgroundColor + board.backgroundColor);
                    tagList.getChildren().add(1, tagUI);
                    tagMap.put(tag.tagId, tagUI);
                }));
    }

    public StompSession.Subscription registerForTagUpdates() {
        return server.registerForMessages("/topic/boards/updateTag/" + board.boardId,
                commons.Tag.class,
                tag -> Platform.runLater(() -> {
                    System.out.println("tag updated: " + tag);
                    board.tags.removeIf(t -> t.tagId == tag.tagId);
                    board.tags.add(tag);
                    Tag tagUI = tagMap.get(tag.tagId);

                    Tag newTag = new Tag(mainCtrl, server, tag, board);
                    newTag.deleteTag.setStyle(fxBackgroundColor + board.backgroundColor);
                    newTag.saveTag.setStyle(fxBackgroundColor + board.backgroundColor);
                    newTag.edit.setStyle(fxBackgroundColor + board.backgroundColor);
                    tagList.getChildren().set(tagList.getChildren().indexOf(tagUI), newTag);
                    tagMap.put(tag.tagId, newTag);

                    listMap.forEach((listId, list) -> {
                        list.getCardMap().forEach((taskId, card) -> {
                            card.updateTag(tag);
                        });
                    });
                }));
    }

    public StompSession.Subscription registerForTagDeletes() {
        return server.registerForMessages("/topic/boards/deleteTag/" + board.boardId, Long.class,
                tagId -> Platform.runLater(() -> {
                    Tag tagUI = tagMap.get(tagId);
                    board.tags.removeIf(t -> t.tagId == tagId);
                    tagList.getChildren().remove(tagUI);

                    listMap.forEach((listId, list) -> {
                        list.getCardMap().forEach((taskId, card) -> {
                            card.removeTag(tagId);
                            card.getTask().tags.removeIf(t -> t.tagId == tagId);
                        });
                    });
                }));
    }


    public void switchToAddTask() {
        mainCtrl.showAddTask();
    }

    public void switchToBoardOverviewScene() {
        customize.setVisible(false);
        subscriptions.forEach(StompSession.Subscription::unsubscribe);
        if (adminMode) {
            mainCtrl.showAdminOverview();
        } else {
            mainCtrl.showBoardOverview();
        }
    }

    public void setBoard(Board board) {
        this.board = board;
        boardName.setText(board.title);
        board_hbox.getChildren().clear();
        listMap = new HashMap<>();
        for (var taskList : board.lists) {
            List list = new List(mainCtrl, server, taskList, this.board);
            list.getScrollPane().setStyle(fxBackgroundColor
                    + board.listsColor + "; -fx-background-radius: 10px;");
            list.getAddButton().setStyle(fxBackgroundColor + board.listsColor + ";");
            list.getTitle().setStyle(fxBackgroundColor + board.listsColor
                    + "; -fx-border-radius: 10px; -fx-background-radius: 10px;" +
                    " -fx-border-color: transparent;");
            list.getTitle().setTextFill(Color.valueOf(board.listsFontColor));
            list.getDeleteTaskListButton().setStyle(fxBackgroundColor + board.listsColor + ";");
            board_hbox.getChildren().add(list);
            listMap.put(taskList.listId, list);
        }

        tagList.getChildren().remove(1, tagList.getChildren().size());
        tagMap = new HashMap<>();
        for (var tag : board.tags) {
            Tag tagUI = new Tag(mainCtrl, server, tag, board);
            tagUI.deleteTag.setStyle(fxBackgroundColor + board.backgroundColor + ";");
            tagUI.edit.setStyle(fxBackgroundColor + board.backgroundColor + ";");
            tagUI.saveTag.setStyle(fxBackgroundColor + board.backgroundColor + ";");
            tagList.getChildren().add(tagUI);
            tagMap.put(tag.tagId, tagUI);
        }

        setBoardColors(board);
        setBoardFontColors(board);
        setCardsColorsLaunch(board);

        newtTitle.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER && newtTitle.isVisible()) saveNewTitle();
        });

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

        getRoot().getScene().setOnKeyPressed(event -> {
            handleShortcuts(event);
            System.out.println(event.getCode());
        });
    }

    public void setCardsColorsLaunch(Board board) {
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            for (int i = 0; i < list.getList().getChildren().size() - 1; ++i) {
                Card card = (Card) list.getList().getChildren().get(i);
                if (board.currentPreset == 0) {
                    loadCardColors(card, board.cardsBackground1, board.cardsFont1);
                } else if (board.currentPreset == 1) {
                    loadCardColors(card, board.cardsBackground2, board.cardsFont2);
                } else {
                    loadCardColors(card, board.cardsBackground3, board.cardsFont3);
                }
            }
        }
    }

    private void loadCardColors(Card card, String background, String font) {
        card.getRoot().setStyle(fxBackgroundColor + background +
                "; -fx-background-radius: 10px;" + " -fx-border-color: ddd;"
                + " -fx-border-radius: 10px ");
        if (font.equals("000000")) {
            card.getRoot().setStyle(fxBackgroundColor + background +
                    "; -fx-background-radius: 10px;" + " -fx-border-color: ddd;"
                    + " -fx-border-radius: 10px ");
        } else {
            card.getRoot().setStyle(fxBackgroundColor + background +
                    "; -fx-background-radius: 10px;" + " -fx-border-color: #"
                    + font + "; -fx-border-radius: 10px ");
        }
        card.getOpenTask().setStyle(fxBackgroundColor + background);
        card.getDeleteTaskButton().setStyle(fxBackgroundColor + background);
        if (font.equals("000000")) {
            card.getTaskTitle().setStyle(fxBackgroundColor + background
                    + "; -fx-border-radius: 3px; -fx-border-color: ddd;");
        } else {
            card.getTaskTitle().setStyle(fxBackgroundColor + background
                    + "; -fx-border-radius: 3px; -fx-border-color: #"
                    + font + ";" + "; -fx-text-fill: #" + font);
        }
    }

    private void setBoardColors(Board board) {
        root.setStyle(fxBackgroundColor + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        save.setStyle(fxBackgroundColor + board.backgroundColor + ";");
        addListTaskVBox.setStyle(fxBackgroundColor
                + board.buttonsBackground + "; -fx-background-radius: 10px;");
        addList.setStyle(fxBackgroundColor + board.buttonsBackground + ";");
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
        customize.setVisible(true);
        blurPane.setVisible(true);
        blurPane.setOnMouseClicked(event -> {
            blurPane.setVisible(false);
            customize.setVisible(false);
        });

        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        colorPickerButtons.setValue(Color.valueOf(board.buttonsBackground));
        colorPickerBackgroundFont.setValue(Color.valueOf(board.backgroundColorFont));
        colorPickerButtonsFont.setValue(Color.valueOf(board.buttonsColorFont));
        colorPickerBoard.setValue(Color.valueOf(board.boardColor));
        colorPickerListsColor.setValue(Color.valueOf(board.listsColor));
        colorPickerListsFont.setValue(Color.valueOf(board.listsFontColor));
        presetB1.setValue(Color.valueOf(board.cardsBackground1));
        presetF1.setValue(Color.valueOf(board.cardsFont1));
        presetB2.setValue(Color.valueOf(board.cardsBackground2));
        presetF2.setValue(Color.valueOf(board.cardsFont2));
        presetB3.setValue(Color.valueOf(board.cardsBackground3));
        presetF3.setValue(Color.valueOf(board.cardsFont3));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));

        if (board.currentPreset == 0) {
            pointer1.setVisible(true);
            pointer2.setVisible(false);
            pointer3.setVisible(false);
        } else if (board.currentPreset == 1) {
            pointer1.setVisible(false);
            pointer2.setVisible(true);
            pointer3.setVisible(false);
        } else {
            pointer1.setVisible(false);
            pointer2.setVisible(false);
            pointer3.setVisible(true);
        }
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
        btnCustomize.setStyle(fxBackgroundColor + buttonColor + ";");
        btnOverviewBoards.setStyle(fxBackgroundColor + buttonColor + ";");
        overviewBoardsPane.setStyle(fxBackgroundColor
                + buttonColor + ";-fx-background-radius: 10px;");
        custimozePane.setStyle(fxBackgroundColor
                + buttonColor + ";-fx-background-radius: 10px;");
        this.board.buttonsBackground = buttonColor;
        //lists color
        board.listsColor = colorPickerListsColor.getValue().toString().substring(2, 8);
        board.listsFontColor = colorPickerListsFont.getValue().toString().substring(2, 8);
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            list.getScrollPane().setStyle(fxBackgroundColor
                    + board.listsColor + "; -fx-background-radius: 10px;");
            list.getAddButton().setStyle(fxBackgroundColor + board.listsColor);
            list.getTitle().setStyle(fxBackgroundColor + board.listsColor
                    + "; -fx-border-radius: 10px; -fx-background-radius: 10px;" +
                    " -fx-border-color: transparent;");
            list.getTitle().setTextFill(Color.valueOf(board.listsFontColor));
            list.getDeleteTaskListButton().setStyle(fxBackgroundColor + board.listsColor + ";");
        }

        for (int i = 1; i < tagList.getChildren().size(); ++i) {
            Tag tag = (Tag) tagList.getChildren().get(i);
            tag.edit.setStyle(fxBackgroundColor + board.backgroundColor);
            tag.deleteTag.setStyle(fxBackgroundColor + board.backgroundColor);
            tag.saveTag.setStyle(fxBackgroundColor + board.backgroundColor);
        }

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
        for (int i = 1; i < tagList.getChildren().size(); ++i) {
            Tag tag = (Tag) tagList.getChildren().get(i);
            tag.edit.setStyle(fxBackgroundColor + board.backgroundColor);
            tag.deleteTag.setStyle(fxBackgroundColor + board.backgroundColor);
            tag.saveTag.setStyle(fxBackgroundColor + board.backgroundColor);
        }
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
        board.buttonsColorFont = "Black";

        updateBoard(board);
        colorPickerButtonsFont.setValue(Color.valueOf("Black"));
    }

    public void resetListsColor() {
        board.listsColor = "ffffff";
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            list.getScrollPane().setStyle(fxBackgroundColor
                    + board.listsColor + "; -fx-background-radius: 10px;");
            list.getAddButton().setStyle(fxBackgroundColor + board.listsColor);
            list.getTitle().setStyle(fxBackgroundColor + "ffffff"
                    + "; -fx-border-radius: 10px; -fx-background-radius: 10px;" +
                    " -fx-border-color: transparent;");
            list.getDeleteTaskListButton().setStyle(fxBackgroundColor + "ffffff;");
        }

        updateBoard(board);
        colorPickerListsColor.setValue(Color.valueOf("ffffff"));
    }

    public void resetListsFont() {
        board.listsFontColor = "000000";
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            list.getTitle().setTextFill(Color.valueOf(board.listsFontColor));
        }

        updateBoard(board);
        colorPickerListsFont.setValue(Color.valueOf(board.listsFontColor));
    }

    public void resetAllColors() {
        resetBoardColor();
        resetBackgroundColor();
        resetButtonColor();
        resetBackgroundFont();
        resetButtonFont();
        resetListsColor();
        resetListsFont();
    }


    public void disable() {
        addList.setDisable(true);
        addTag.setDisable(true);
        btnCustomize.setDisable(true);
        editTitle.setDisable(true);
    }

    public void enable() {
        addList.setDisable(false);
        addTag.setDisable(false);
        btnCustomize.setDisable(false);
        editTitle.setDisable(false);
    }

    public void apply1() {
        String background = presetB1.getValue().toString().substring(2, 8);
        String font = presetF1.getValue().toString().substring(2, 8);
        board.cardsBackground1 = background;
        board.cardsFont1 = font;
        board.currentPreset = 0;
        setCardsColors(background, font);
        updateBoard(board);

        pointer1.setVisible(true);
        pointer2.setVisible(false);
        pointer3.setVisible(false);
    }

    public void apply2() {
        String background = presetB2.getValue().toString().substring(2, 8);
        String font = presetF2.getValue().toString().substring(2, 8);
        board.cardsBackground2 = background;
        board.cardsFont2 = font;
        board.currentPreset = 1;
        setCardsColors(background, font);
        updateBoard(board);

        pointer1.setVisible(false);
        pointer2.setVisible(true);
        pointer3.setVisible(false);
    }

    public void apply3() {
        String background = presetB3.getValue().toString().substring(2, 8);
        String font = presetF3.getValue().toString().substring(2, 8);
        board.cardsBackground3 = background;
        board.cardsFont3 = font;
        board.currentPreset = 2;
        setCardsColors(background, font);
        updateBoard(board);

        pointer1.setVisible(false);
        pointer2.setVisible(false);
        pointer3.setVisible(true);
    }

    public void reset1() {
        board.cardsBackground1 = "ffffff";
        board.cardsFont1 = "000000";
        if (board.currentPreset == 0) setCardsColors(board.cardsBackground1, board.cardsFont1);
        presetB1.setValue(Color.valueOf(board.cardsBackground1));
        presetF1.setValue(Color.valueOf(board.cardsFont1));
        updateBoard(board);
    }

    public void reset2() {
        board.cardsBackground2 = "ffffff";
        board.cardsFont2 = "000000";
        if (board.currentPreset == 1) setCardsColors(board.cardsBackground2, board.cardsFont2);
        presetB2.setValue(Color.valueOf(board.cardsBackground2));
        presetF2.setValue(Color.valueOf(board.cardsFont2));
        updateBoard(board);
    }

    public void reset3() {
        board.cardsBackground3 = "ffffff";
        board.cardsFont3 = "000000";
        if (board.currentPreset == 2) setCardsColors(board.cardsBackground3, board.cardsFont3);
        presetB3.setValue(Color.valueOf(board.cardsBackground3));
        presetF3.setValue(Color.valueOf(board.cardsFont3));
        updateBoard(board);
    }

    public void setCardsColors(String background, String font) {
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            for (int i = 0; i < list.getList().getChildren().size() - 1; ++i) {
                Card card = (Card) list.getList().getChildren().get(i);
                if (font.equals("000000")) {
                    card.getRoot().setStyle(fxBackgroundColor + background +
                            "; -fx-background-radius: 10px;" + " -fx-border-color: ddd;"
                            + " -fx-border-radius: 10px ");
                } else {
                    card.getRoot().setStyle(fxBackgroundColor + background +
                            "; -fx-background-radius: 10px;" + " -fx-border-color: #"
                            + font + "; -fx-border-radius: 10px ");
                }
                card.getOpenTask().setStyle(fxBackgroundColor + background);
                card.getDeleteTaskButton().setStyle(fxBackgroundColor + background);
                if (font.equals("000000")) {
                    card.getTaskTitle().setStyle(fxBackgroundColor + background
                            + "; -fx-border-radius: 3px; -fx-border-color: ddd;");
                } else {
                    card.getTaskTitle().setStyle(fxBackgroundColor + background
                            + "; -fx-border-radius: 3px; -fx-border-color: #" + font
                            + "; -fx-text-fill: #" + font);
                }
            }
        }
    }

    public AnchorPane getRoot() {
        return root;
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void displayDetailedTask(DetailedTask detailedTask) {
        detailedTask.setStyle("-fx-background-radius: 20");
        detailedTask.setLayoutX(150);
        detailedTask.setLayoutY(100);
        blurPane.setVisible(true);
        blurPane.setOnMouseClicked(event -> { } );
        root.getChildren().add(detailedTask);
    }

    public void stopDisplayingDialog(DetailedTask detailedTask) {
        blurPane.setVisible(false);
        root.getChildren().remove(detailedTask);
    }
}
