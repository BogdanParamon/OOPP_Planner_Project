package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Subtask;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;

public class DetailedTask extends AnchorPane {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Board board;
    private final TaskList taskList;
    private final Task task;

    @FXML
    private VBox tasks_vbox;

//    @FXML
//    private VBox tags_vbox;

    @FXML
    private TextField dtvTitle;

    @FXML
    private TextArea dtvDescription;

    @FXML
    private MFXButton addSubtaskButton;

    @FXML
    private MFXButton doneButton;

    @FXML
    private Text readOnly;

    private HashMap<Long, client.scenes.Subtask> subtaskMap;

    /**
     * Setup server and main controller
     *
     * @param mainCtrl the main controller
     * @param server   server to connect to
     * @param board    the board related to the detailed view
     * @param taskList the taskList related to the detailed view
     * @param task     the task related to the detailed view
     */
    public DetailedTask(MainCtrl mainCtrl, ServerUtils server, Board board,
                        TaskList taskList, Task task) {

        this.mainCtrl = mainCtrl;
        this.server = server;
        this.board = board;
        this.taskList = taskList;
        this.task = task;

        FXMLLoader loader =
                new FXMLLoader(getClass()
                        .getResource("/client/scenes/Components/DetailedTask.fxml"));
        loader.setRoot(this);
        loader.setController(DetailedTask.this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        subtaskMap = new HashMap<>();
        for (Subtask subtask : this.task.subtasks) {
            client.scenes.Subtask subtaskUI =
                    new client.scenes.Subtask(mainCtrl, server, board, taskList, task, subtask);
            subtaskUI.getCheckbox().setSelected(subtask.subtaskBoolean);
            tasks_vbox.getChildren().add(0, subtaskUI);
            subtaskMap.put(subtask.subTaskId, subtaskUI);
        }

        dtvTitle.setText(task.title);
        dtvDescription.setText(task.description);

        doneButton.setOnAction(event -> stopDisplayingDialog());

        addSubtaskButton.setOnAction(event -> addSubtask());

        readOnly = new Text("You are in read-only mode.");
        readOnly.setFill(Color.color(1,0,0));
        readOnly.setX(230);
        readOnly.setY(40);
        readOnly.setVisible(false);
        this.getChildren().add(readOnly);


        initEditTaskTitle();
        initEditTaskDescription();

    }

    public void addSubtask() {
        Subtask subtask = new Subtask("New Subtask");
        server.send("/app/subtasks/add/" + board.boardId + "/"
                + taskList.listId + "/" + task.taskId, subtask);
    }

    public VBox getTasks_vbox() {
        return tasks_vbox;
    }

    public void stopDisplayingDialog() {
        Card card = new Card(mainCtrl, server, task, taskList, board);
        card.setHasDetailedTaskOpen(false);
        mainCtrl.boardCtrl.stopDisplayingDialog(this);
    }

    public boolean hasTaskDescription() {
        return !task.description.trim().equals("");
    }

    public void updateDetails() {

    }

    void initEditTaskTitle() {
        dtvTitle.setOnKeyReleased(event -> handleKeyReleaseTitle(event));
        dtvTitle.focusedProperty().addListener(this::handleFocusChangeTitle);
    }

    void initEditTaskDescription() {
        dtvDescription.focusedProperty().addListener(this::handleFocusChangeDescription);
    }

    private void handleKeyReleaseTitle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            dtvTitle.getParent().requestFocus();
            saveTaskTitleFromDetailedTaskView();
        }
    }

    private void handleFocusChangeTitle(ObservableValue<? extends Boolean>
                                                observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            saveTaskTitleFromDetailedTaskView();
        }
    }

    private void handleFocusChangeDescription(ObservableValue<? extends Boolean>
                                                      observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            saveTaskDescription();
        }
    }

    protected void disable() {
        dtvDescription.setDisable(true);
        dtvTitle.setDisable(true);
        addSubtaskButton.setDisable(true);
        for (Node node : tasks_vbox.getChildren()) {
            node.setDisable(true);
        }
        readOnly.setVisible(true);
        ///tags_vbox.setDisable(true);
    }

    protected void enable() {
        dtvDescription.setDisable(false);
        dtvTitle.setDisable(false);
        addSubtaskButton.setDisable(false);
        for (Node node : tasks_vbox.getChildren()) {
            node.setDisable(false);
        }
        readOnly.setVisible(false);
        ///tags_vbox.setDisable(false);
    }

    private void saveTaskTitleFromDetailedTaskView() {
        task.title = dtvTitle.getText();
        server.send("/app/tasks/update/" + board.boardId + "/" + taskList.listId, task);
    }

    private void saveTaskDescription() {
        task.description = dtvDescription.getText();
        server.send("/app/tasks/update/" + board.boardId + "/" + taskList.listId, task);
    }

    public TextArea getDtvDescription() {
        return dtvDescription;
    }

    public TextField getDtvTitle() {
        return dtvTitle;
    }

    public HashMap<Long, client.scenes.Subtask> getSubtaskMap() {
        return subtaskMap;
    }
}
