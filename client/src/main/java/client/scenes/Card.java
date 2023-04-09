package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Packet;
import commons.Subtask;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.IOException;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final Board board;
    private TaskList taskList;
    private Task task;
    private DetailedTask detailedTask;
    private int row = 0;
    private int col = 0;

    public static Card focused = null;

    public boolean isDetailedTaskOpen = false;
    @FXML
    private MFXButton deleteTaskButton;
    @FXML
    private TextField taskTitle;
    @FXML
    private MFXButton openTask;
    @FXML
    private GridPane tags;
    @FXML
    private Pane root;
    @FXML
    private ImageView descriptionImage;
    @FXML
    private MFXProgressBar progressBar;
    @FXML
    private Label progressLabel;

    private static long dragFromListId;
    private static long dragToListId;
    private static int dragToIndex;

    private boolean hasDetailedTaskOpen = false;

    /**
     * Constructs a new Card instance with the specified parameters.
     *
     * @param mainCtrl The MainCtrl instance that manages the main application view.
     * @param server   The ServerUtils instance for handling server communication.
     * @param task     The Task instance representing the task to be displayed in the card.
     * @param taskList The TaskList instance containing the task.
     * @param board    The Board instance containing the taskList.
     */
    public Card(MainCtrl mainCtrl, ServerUtils server, Task task, TaskList taskList, Board board) {

        this.mainCtrl = mainCtrl;
        this.server = server;
        this.task = task;
        this.taskList = taskList;
        this.board = board;

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/Card.fxml"));
        loader.setRoot(this);
        loader.setController(Card.this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        detailedTask = new DetailedTask(mainCtrl, server, board, taskList, task);

        deleteTaskButton.setOnAction(event -> deleteTask());

        taskTitle.setText(task.title);
        task.tags.forEach(tag -> addTag(tag, false));

        initDrag();
        initEditTaskTitle();

        registerForAddTagMessages();
        registerForDeleteTagMessages();
        

        openTask.setOnAction(event -> {
            hasDetailedTaskOpen = true;
            displayDialog();
        });

        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                displayDialog();
        });

        updateProgress();

        this.setOnMouseClicked(event -> {
            if (focused != null && focused != this) {
                focused.setStyle(focused.getStyle().replace("blue", "ddd"));
            }
            setStyle(getStyle().replace("ddd", "blue"));
            focused = this;
        });
    }

    enum Direction {
        UP, DOWN;
    }

    public void simulateDragAndDrop(Direction direction) {

        System.out.println(direction);
        VBox parent = (VBox) getParent();
        int currentIndex = parent.getChildren().indexOf(this);
        int newIndex = direction == Direction.UP ? currentIndex - 1 : currentIndex + 1;

        System.out.println(currentIndex);
        System.out.println(newIndex);

        if (newIndex >= 0 && newIndex < parent.getChildren().size() - 1) {
            parent.getChildren().remove(this);
            parent.getChildren().add(newIndex, this);

            Packet packet = new Packet();
            packet.longValue = task.taskId;
            packet.longValue2 = taskList.listId;
            packet.longValue3 = taskList.listId;
            packet.intValue = newIndex;
            server.send("/app/tasks/drag/" + board.boardId, packet);
        }
    }

    public void showAddTagPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.initOwner(this.getScene().getWindow());

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        Label label = new Label("Enter tag name:");
        TextField tagNameInput = new TextField();

        Label colorLabel = new Label("Choose tag color:");
        ColorPicker colorPicker = new ColorPicker();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String tagName = tagNameInput.getText().trim();
            Color tagColor = colorPicker.getValue();
            if (!tagName.isEmpty() && tagColor != null) {
                addTagToTask(tagName, tagColor);

                popupStage.close();
            }
        });

        vbox.getChildren().addAll(label, tagNameInput, colorLabel, colorPicker, addButton);
        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);

        popupStage.show();
    }

    public void addTagToTask(String tagName, Color tagColor) {
        commons.Tag newTag = new commons.Tag();
        newTag.setText(tagName);
        newTag.setColor("#" + tagColor.toString().substring(2, 8));

        addTag(newTag, true);
    }



    public StompSession.Subscription registerForAddTagMessages() {
        return server.registerForMessages("/topic/tasks/addTag/" + task.taskId, commons.Tag.class,
                tag -> {
                    Platform.runLater(() -> {
                        addTag( tag, true);
                        getDetailedTask().getTags_vbox()
                                .getChildren().add(new Tag(mainCtrl, server, tag, board));
                        }
                    );
                });
    }

    public StompSession.Subscription registerForDeleteTagMessages() {
        return server.registerForMessages("/topic/tasks/deleteTag/"
                        + task.taskId, commons.Packet.class,
                packet -> {
                    Platform.runLater(() -> {
                        long tagId = packet.longValue;
                        for (Node node : getDetailedTask().getTags_vbox().getChildren()) {
                            if (!(node instanceof Tag)) continue;
                            Tag tag = (Tag) node;
                            if (tag.getTagId() == tagId) {
                                getDetailedTask().getTags_vbox().getChildren().remove(tag);
                                break;
                            }
                        }
                        removeTag(tagId);
                    }
                    );
                });
    }


    void initDrag() {
        int[] index = {0};
        VBox[] orignalParent = new VBox[1];

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.taskId));

            // clever way to  work around variables in lambda
            orignalParent[0] = (VBox) getParent();
            index[0] = ((VBox) getParent()).getChildren().indexOf(this);
            dragFromListId = taskList.listId;

            mainCtrl.boardCtrl.getRoot().getChildren().add(this); // find a better way for this
            setVisible(false);

            db.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (!event.getGestureSource().getClass().equals(client.scenes.Tag.class))
                return;
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        setOnDragDropped(event -> {
            if (!event.getGestureSource().getClass().equals(client.scenes.Tag.class))
                return;

            Dragboard db = event.getDragboard();
            long tagId = Long.parseLong(db.getString());
            commons.Tag tag = server.getTagById(tagId);
            if (!task.tags.contains(tag)) {
                server.send("/app/tasks/addTag/" + task.taskId, tag);
            }
            event.setDropCompleted(true);
        });

        setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE
                    && !(dragFromListId == dragToListId && index[0] == dragToIndex)) {
                Packet packet = new Packet();
                packet.longValue = task.taskId;
                packet.longValue2 = dragFromListId;
                packet.longValue3 = dragToListId;
                packet.intValue = dragToIndex;
                server.send("/app/tasks/drag/" + board.boardId, packet);
            }
            orignalParent[0].getChildren().add(index[0], this);
            setVisible(true);
            mainCtrl.boardCtrl.getRoot().getChildren().remove(this);
            dragFromListId = 0;
            dragToListId = 0;
            event.consume();
        });
    }

    void deleteTask() {
        server.send("/app/tasks/delete/" + board.boardId + "/" + taskList.listId, task.taskId);
    }

    void initEditTaskTitle() {
        taskTitle.setOnKeyReleased(event -> handleKeyRelease(event));
        taskTitle.focusedProperty().addListener(this::handleFocusChange);
    }


    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            taskTitle.getParent().requestFocus();
            saveTaskTitle();
        }
    }

    void editTaskTitle() {
        taskTitle.requestFocus();
        taskTitle.selectAll();
    }


    void displayDialog() {
        detailedTask.updateDetails();
        mainCtrl.boardCtrl.displayDetailedTask(detailedTask);
        isDetailedTaskOpen = true;
    }

    void closeDialog() {
        mainCtrl.boardCtrl.stopDisplayingDialog(detailedTask);
        isDetailedTaskOpen = false;
    }

    private void handleFocusChange(ObservableValue<? extends Boolean>
                                           observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            saveTaskTitle();
        }
    }

    private void saveTaskTitle() {
        task.title = taskTitle.getText();
        server.send("/app/tasks/update/" + board.boardId + "/" + taskList.listId, task);
    }

    public void addTag(commons.Tag tag, boolean newTag) {
        Pane pane = new Pane();
        pane.setId(String.valueOf(tag.tagId));
        pane.setPrefSize(20, 7);
        pane.setStyle("-fx-background-radius: 5; -fx-background-color: " + tag.getColor());

        if (newTag) {
            task.addTag(tag);
        }
        tags.add(pane, col, row);
        col = (col + 1) % 4;
        if (col == 0) row++;
    }

    public void removeTag(long tagId) {
        task.tags.removeIf(tag -> tag.tagId == tagId);
        getDetailedTask().getTags_vbox().getChildren()
                .removeIf(node -> node instanceof Tag && ((Tag) node).tag.tagId == tagId);
        tags.getChildren().removeIf(node -> node.getId().equals(String.valueOf(tagId)));
    }

    public void updateTag(commons.Tag tag) {
        for (Node node : tags.getChildren()) {
            if (node.getId().equals(String.valueOf(tag.tagId))) {
                node.setStyle("-fx-background-radius: 5; -fx-background-color: " + tag.getColor());
            }
        }
        for (int i = 0; i < getDetailedTask().getTags_vbox().getChildren().size(); i++) {
            if (getDetailedTask().getTags_vbox().getChildren().get(i) instanceof Tag) {
                Tag tagUI = (Tag) getDetailedTask().getTags_vbox().getChildren().get(i);
                if (tagUI.tag.tagId == tag.tagId) {
                    getDetailedTask().getTags_vbox()
                            .getChildren().set(i, new Tag(mainCtrl, server, tag, board));
                    break;
                }
            }
        }
    }

    public Task getTask() {
        return task;
    }

    public TextField getTaskTitle() {
        return taskTitle;
    }

    public DetailedTask getDetailedTask() {
        return detailedTask;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public static void setDragToListId(long dragToListId) {
        Card.dragToListId = dragToListId;
    }

    public static void setDragToIndex(int dragToIndex) {
        Card.dragToIndex = dragToIndex;
    }

    public Pane getRoot() {
        return root;
    }

    public MFXButton getDeleteTaskButton() {
        return deleteTaskButton;
    }

    public MFXButton getOpenTask() {
        return openTask;
    }

    public void setHasDetailedTaskOpen(boolean hasDetailedTaskOpen) {
        this.hasDetailedTaskOpen = hasDetailedTaskOpen;
    }

    public boolean isHasDetailedTaskOpen() {
        return hasDetailedTaskOpen;
    }

    public void showDescriptionImage() {
        descriptionImage.setVisible(true);
    }

    public void hideDescriptionImage() {
        descriptionImage.setVisible(false);
    }

    public void updateProgress() {
        progressLabel.setText(currentProgress());
        progressBar.setProgress(currentProgressDouble());
    }

    public String currentProgress() {
        int total = task.subtasks.size();
        int done = 0;
        for (Subtask subtask : task.subtasks)
            if (subtask.subtaskBoolean) done++;
        return done + "/" + total;
    }

    public double currentProgressDouble() {
        double total = task.subtasks.size();
        double done = 0.0;
        for (Subtask subtask : task.subtasks)
            if (subtask.subtaskBoolean) done++;
        return done / total;
    }
}
