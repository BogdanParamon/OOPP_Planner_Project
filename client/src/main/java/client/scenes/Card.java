package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Packet;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.IOException;
import java.net.URL;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final Board board;
    private TaskList taskList;
    private Task task;
    private int row = 0;
    private int col = 0;
    @FXML
    private MFXButton deleteTaskButton;
    @FXML
    private TextField taskTitle;
    @FXML
    private MFXButton openTask;

    @FXML
    private GridPane tags;

    private static long dragFromListId;
    private static long dragToListId;
    private static int dragToIndex;

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

        deleteTaskButton.setOnAction(event -> deleteTask());

        taskTitle.setText(task.title);
        task.tags.forEach(tag -> addTag(tag, false));

        initDrag();
        initEditTaskTitle();

        registerForAddTagMessages();

        openTask.setOnAction(event -> displayDialog());

        URL cssURL = getClass().getResource("/client/scenes/Components/Cardstyle.css");
        if (cssURL != null) {
            String cssPath = cssURL.toExternalForm();
            getStylesheets().add(cssPath);
        } else {
            System.out.println("Can not load Cardstyle.css");
        }
    }

    public StompSession.Subscription registerForAddTagMessages() {
        return server.registerForMessages("/topic/tasks/addTag/" + task.taskId, commons.Tag.class,
                tag -> {
                    Platform.runLater(() -> addTag( tag, true)
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

    void displayDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        DetailedTask detailedTask = new DetailedTask(mainCtrl, server, task);

        AnchorPane root = detailedTask;
        stage.setScene(new Scene(root));
        stage.show();
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
        tags.getChildren().removeIf(node -> node.getId().equals(String.valueOf(tagId)));
    }

    public void updateTag(commons.Tag tag) {
        for (Node node : tags.getChildren()) {
            if (node.getId().equals(String.valueOf(tag.tagId))) {
                node.setStyle("-fx-background-radius: 5; -fx-background-color: " + tag.getColor());
            }
        }
    }

    public Task getTask() {
        return task;
    }

    public TextField getTaskTitle() {
        return taskTitle;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        System.out.println(this.toString() + this.taskList.listId + " " + taskList.listId);
        this.taskList = taskList;
    }

    public static void setDragToListId(long dragToListId) {
        Card.dragToListId = dragToListId;
    }

    public static void setDragToIndex(int dragToIndex) {
        Card.dragToIndex = dragToIndex;
    }
}
