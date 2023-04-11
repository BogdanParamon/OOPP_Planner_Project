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
import javafx.scene.control.Label;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.messaging.simp.stomp.StompSession;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
    @FXML MFXButton copyBtn;
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

    @FXML
    private Pane blurPane;

    @FXML
    private MFXButton passwordButton;

    private boolean isEnabled;

    @FXML private Pane shortcutsPane;

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
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(mainCtrl.password);
        mainCtrl.passwordCtrl.setStage(stage);
        stage.show();
    }

    protected void setPassword(Board board, User user) {
        mainCtrl.passwordCtrl.setBoard(board);
        mainCtrl.passwordCtrl.setUser(user);
        mainCtrl.passwordCtrl.setMode(false);
        mainCtrl.passwordCtrl.setUp();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(mainCtrl.password);
        mainCtrl.passwordCtrl.setStage(stage);
        stage.show();
    }

    public void handleShortcutKeys(KeyEvent event) {
        if (event.isShiftDown() && event.getCode() == KeyCode.SLASH && !shortcutsPane.isVisible()) {
            blurPane.setVisible(true);
            shortcutsPane.setVisible(true);
            blurPane.setOnMouseClicked(event1 -> {
                blurPane.setVisible(false);
                shortcutsPane.setVisible(false);
            });
        }

        if (Card.focused == null) return;

        if (event.isShiftDown()) {
            if (event.getCode() == KeyCode.UP) {
                Card.focused.simulateDragAndDrop(Card.Direction.UP);
            } else if (event.getCode() == KeyCode.DOWN) {
                Card.focused.simulateDragAndDrop(Card.Direction.DOWN);
            }
        } else if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
            Card.focused.deleteTask();
        } else if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
            for (Node node : root.getChildren()) {
                if (node instanceof DetailedTask) {
                    return;
                }
            }
            Card.focused.displayDialog();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            for (Node node : root.getChildren()) {
                if (node instanceof DetailedTask) {
                    stopDisplayingDialog((DetailedTask) node);
                    break;
                }
            }
        } else if (event.getCode() == KeyCode.E) {
            Card.focused.editTaskTitle();
        } else if (event.getCode() == KeyCode.C) {
            showCustomize();
        } else if (event.getCode() == KeyCode.T) {
            displayTagSelectionPane();
        }
    }

    public void displayTagSelectionPane() {
        Pane pane = new Pane();
        pane.setPrefSize(120, 200);
        pane.setStyle("-fx-background-color: white; -fx-background-radius: 10px");
        pane.setLayoutX(400);
        pane.setLayoutY(250);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setLayoutX(10);

        for (var tag : board.tags) {
            Pane tagPane = new Pane();
            tagPane.setPrefSize(85, 20);
            tagPane.setStyle("-fx-background-color: "
                    + tag.getColor() + "; -fx-background-radius: 10px");
            Label text = new Label(tag.getText());
            text.setFont(Font.font("Roboto", 13));
            text.setLayoutX(5);
            text.setLayoutY(3);
            tagPane.getChildren().add(text);

            tagPane.setOnMouseClicked(event1 -> {
                if (Card.focused.getTask().tags.contains(tag))  return;
                server.send("/app/tasks/addTag/" + Card.focused.getTask().taskId, tag);
                blurPane.setVisible(false);
                root.getChildren().remove(pane);
            });

            vbox.getChildren().add(tagPane);
        }

        MFXScrollPane scrollPane = new MFXScrollPane();
        scrollPane.setPrefHeight(180);
        scrollPane.setPrefWidth(110);
        scrollPane.setLayoutX(5);
        scrollPane.setLayoutY(10);
        scrollPane.setContent(vbox);

        pane.getChildren().add(scrollPane);

        blurPane.setVisible(true);
        blurPane.setOnMouseClicked(event1 -> {
            blurPane.setVisible(false);
            root.getChildren().remove(pane);
        });
        root.getChildren().add(root.getChildren().size() - 1, pane);
    }

    public void handleControlKeys(KeyEvent event) {
        if (Card.focused == null) return;

        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
                    || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                handleControlArrowKeys(event);
            }
        }
    }

    private void handleControlArrowKeys(KeyEvent event) {
        VBox parent = (VBox) Card.focused.getParent();
        int cardIndex = parent.getChildren().indexOf(Card.focused);

        int listIndex = -1;
        for (int i = 0; i < board_hbox.getChildren().size(); i++) {
            List list = (List) board_hbox.getChildren().get(i);
            if ( list.getList().equals(parent)) {
                listIndex = i;
                break;
            }
        }

        if (event.getCode() == KeyCode.UP) {
            if (cardIndex > 0) {
                updateCardFocus((Card) parent.getChildren().get(cardIndex - 1));
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            if (cardIndex < parent.getChildren().size() - 2) {
                updateCardFocus((Card) parent.getChildren().get(cardIndex + 1));
            }
        } else if (event.getCode() == KeyCode.RIGHT) {
            if (listIndex == -1 || listIndex == board_hbox.getChildren().size() - 1) return;
            List list = (List) board_hbox.getChildren().get(listIndex + 1);
            if (list.getList().getChildren().size() == 1) return;
            if (list.getList().getChildren().size() <= cardIndex + 1) {
                updateCardFocus((Card)
                        list.getList().getChildren().get(list.getList().getChildren().size() - 2));
            } else {
                updateCardFocus((Card) list.getList().getChildren().get(cardIndex));
            }
        } else if (event.getCode() == KeyCode.LEFT) {
            if (listIndex <= 0) return;
            List list = (List) board_hbox.getChildren().get(listIndex - 1 );
            if (list.getList().getChildren().size() == 1) return;
            if (list.getList().getChildren().size() <= cardIndex + 1) {
                updateCardFocus((Card)
                        list.getList().getChildren().get(list.getList().getChildren().size() - 2));
            } else {
                updateCardFocus((Card) list.getList().getChildren().get(cardIndex));
            }
        }
    }

    private void updateCardFocus(Card newFocusedCard) {
        Card.focused.setStyle(Card.focused.getStyle().replace("blue", "ddd"));
        Card.focused = newFocusedCard;
        Card.focused.setStyle(Card.focused.getStyle().replace("ddd", "blue"));
    }

    public StompSession.Subscription registerForNewLists() {
        return server.registerForMessages("/topic/taskLists/add/" + board.boardId, TaskList.class,
                taskList -> Platform.runLater(() -> {
                    List listUI = new List(mainCtrl, server, taskList, this.board);
                    if (!mainCtrl.boardOverviewCtrl.knowsPassword(user, board))
                        listUI.disable();
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
                    List listUI = listMap.get(listIdAndNewTitle.longValue);
                    listUI.setTitle(listIdAndNewTitle.stringValue);
                    listUI.getTaskList().title = listIdAndNewTitle.stringValue;
                }));
    }

    public StompSession.Subscription registerForBoardRenames() {
        return server.registerForMessages("/topic/boards/rename/" + board.boardId, Packet.class,
                boardIdAndNewTitle -> Platform.runLater(() -> {
                    boardName.setText(boardIdAndNewTitle.stringValue);
                    board.title = boardIdAndNewTitle.stringValue;
                }));
    }

    public StompSession.Subscription registerForBoardUpdates() {
        return server.registerForMessages("/topic/boards/update/" + board.boardId, Board.class,
                updatedBoard -> Platform.runLater(() -> {
                    setBoardColors(updatedBoard);
                    setBoardFontColors(updatedBoard);
                    setCardsColorsLaunch(updatedBoard);
                    setColorPickersAndPresets(updatedBoard);
                    applyChangesListsAndTags(updatedBoard);
                    board.backgroundColor = updatedBoard.backgroundColor;
                    board.buttonsBackground = updatedBoard.buttonsBackground;
                    board.backgroundColorFont = updatedBoard.backgroundColorFont;
                    board.buttonsColorFont = updatedBoard.buttonsColorFont;
                    board.boardColor = updatedBoard.boardColor;
                    board.listsColor = updatedBoard.listsColor;
                    board.listsFontColor = updatedBoard.listsFontColor;
                    board.cardsBackground1 = updatedBoard.cardsBackground1;
                    board.cardsBackground2 = updatedBoard.cardsBackground2;
                    board.cardsBackground3 = updatedBoard.cardsBackground3;
                    board.cardsFont1 = updatedBoard.cardsFont1;
                    board.cardsFont2 = updatedBoard.cardsFont2;
                    board.cardsFont3 = updatedBoard.cardsFont3;
                    if (updatedBoard.currentPreset == 0) {
                        board.currentPreset = 0;
                        pointer1.setVisible(true);
                        pointer2.setVisible(false);
                        pointer3.setVisible(false);
                    } else if (updatedBoard.currentPreset == 1) {
                        board.currentPreset = 1;
                        pointer1.setVisible(false);
                        pointer2.setVisible(true);
                        pointer3.setVisible(false);
                    } else {
                        board.currentPreset = 2;
                        pointer1.setVisible(false);
                        pointer2.setVisible(false);
                        pointer3.setVisible(true);
                    }
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
                    if (!mainCtrl.boardOverviewCtrl.knowsPassword(user, board))
                        card.disable();
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
                    card.getTask().subtasks = task.subtasks;
                    card.getTask().title = task.title;
                    card.getTask().description = task.description;

                    card.getTaskTitle().setText(task.title);
                    card.getDetailedTask().getDtvDescription().setText(task.description);
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
                    card.updateProgress();
                    card.getDetailedTask().getSubtaskMap().put(subtask.subTaskId, UISubtask);
                }));
    }

    public StompSession.Subscription registerForSubtaskRename() {
        return server.registerForMessages("/topic/subtasks/rename/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue2;
                    commons.Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;

                    Subtask subtaskUI = listMap.get(listId)
                            .getCardMap().get(taskId)
                            .getDetailedTask().getSubtaskMap().get(subtask.subTaskId);
                    subtaskUI.getCheckbox().setText(subtask.subtaskText);
                    subtaskUI.getSubtask().subtaskText = subtask.subtaskText;
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
                    card.getDetailedTask().getSubtaskMap().remove(subtaskId);
                    card.getTask().subtasks.remove(subtaskUI.getSubtask());
                    card.updateProgress();
                }));
    }

    public StompSession.Subscription registerForSubtaskStatus() {
        return server.registerForMessages("/topic/subtasks/status/" + board.boardId, Packet.class,
                taskIdlistIdNewTitleAndSubtask -> Platform.runLater(() -> {
                    commons.Subtask subtask = taskIdlistIdNewTitleAndSubtask.subtask;
                    long listId = taskIdlistIdNewTitleAndSubtask.longValue;
                    long taskId = taskIdlistIdNewTitleAndSubtask.longValue2;

                    Card card = listMap.get(listId).getCardMap().get(taskId);
                    Subtask subtaskUI = card.getDetailedTask()
                            .getSubtaskMap().get(subtask.subTaskId);
                    subtaskUI.getCheckbox().setSelected(subtask.subtaskBoolean);
                    subtaskUI.getSubtask().subtaskBoolean = subtask.subtaskBoolean;
                    card.updateProgress();
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
        subscriptions.add(registerForBoardUpdates());

        getRoot().getScene().setOnKeyPressed(event -> {
            handleShortcutKeys(event);
            handleControlKeys(event);
        });

        getRoot().requestFocus();
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
        passwordButton.setStyle(fxBackgroundColor + board.buttonsBackground);
        copyBtn.setStyle(fxBackgroundColor + board.backgroundColor);
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
        passwordButton.setTextFill(Paint.valueOf(board.buttonsColorFont));
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
            server.send("/app/boards/update/" + board.boardId, board);
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

        setColorPickersAndPresets(board);

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

    private void setColorPickersAndPresets(Board board) {
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
        passwordButton.setStyle(fxBackgroundColor + buttonColor);
        copyBtn.setStyle(fxBackgroundColor + rootColor);
        this.board.buttonsBackground = buttonColor;
        //lists and tags color
        applyChangesListsAndTags(this.board);

        applyChangesFont();

        updateBoard(board);
    }

    private void applyChangesListsAndTags(Board board) {
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
    }

    public void applyChangesFont() {
        //background font
        String backgroundFontColor = colorPickerBackgroundFont
                .getValue().toString().substring(2, 8);
        logo.setFill(Paint.valueOf(backgroundFontColor));
        boardName.setFill(Paint.valueOf(backgroundFontColor));
        board.backgroundColorFont = backgroundFontColor;
        txtTags.setFill(Paint.valueOf(backgroundFontColor));
        //buttons font
        String buttonsFontColor = colorPickerButtonsFont.getValue().toString().substring(2, 8);
        btnCustomize.setTextFill(Paint.valueOf(buttonsFontColor));
        btnOverviewBoards.setTextFill(Paint.valueOf(buttonsFontColor));
        addList.setTextFill(Paint.valueOf(buttonsFontColor));
        passwordButton.setTextFill(Paint.valueOf(buttonsFontColor));
        board.buttonsColorFont = buttonsFontColor;
    }

    public void resetBackgroundColor() {
        this.board.backgroundColor = "ffffff";
        root.setStyle(fxBackgroundColor + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #ffffff;");
        save.setStyle("-fx-background-color: #ffffff;");
        tagsPane.setStyle("-fx-background-color: white;");
        addTag.setStyle("-fx-background-color: white;");
        copyBtn.setStyle(fxBackgroundColor + "ffffff");
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
        passwordButton.setStyle(fxBckgroundColorDDD);
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
        passwordButton.setTextFill(Paint.valueOf("Black"));
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
        reset1();
        reset2();
        reset3();
    }


    public void disable() {
        isEnabled = false;
        addList.setDisable(true);
        addTag.setDisable(true);
        btnCustomize.setDisable(true);
        editTitle.setDisable(true);
        tagsPane.setDisable(true);
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            list.disable();
        }
    }

    public void enable() {
        isEnabled = true;
        addList.setDisable(false);
        addTag.setDisable(false);
        btnCustomize.setDisable(false);
        editTitle.setDisable(false);
        tagsPane.setDisable(false);
        for (Node node : board_hbox.getChildren()) {
            List list = (List) node;
            list.enable();
        }
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
        blurPane.setOnMouseClicked(event -> {
        });
        if (!isEnabled)
            detailedTask.disable();
        else detailedTask.enable();
        root.getChildren().add(detailedTask);
    }

    public void stopDisplayingDialog(DetailedTask detailedTask) {
        blurPane.setVisible(false);
        root.getChildren().remove(detailedTask);
    }

    public void copyBoardId() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Long longId = Long.valueOf(board.boardId);
        StringSelection stringId = new StringSelection(longId.toString());
        clipboard.setContents(stringId, null);
    }
}
