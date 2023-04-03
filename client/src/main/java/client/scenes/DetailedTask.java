package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Subtask;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;

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

    /**
     * Setup server and main controller
     * @param mainCtrl the main controller
     * @param server server to connect to
     * @param board the board related to the detailed view
     * @param taskList the taskList related to the detailed view
     * @param task the task related to the detailed view
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

        for (Subtask subtask : this.task.subtasks) {
            client.scenes.Subtask subtaskUI =
                    new client.scenes.Subtask(mainCtrl, server, board, taskList, task, subtask);
            subtaskUI.getCheckbox().setSelected(subtask.subtaskBoolean);
            tasks_vbox.getChildren().add(0, subtaskUI);
        }

        dtvTitle.setText(task.title);
        dtvDescription.setText("Add description");

        doneButton.setOnAction(event -> stopDisplayingDialog());

        addSubtaskButton.setOnAction(event -> addSubtask());

    }

    public void addSubtask() {
        Subtask subtask = new Subtask("New Subtask");
        server.send("/app/subtasks/add/" + board.boardId + "/"
                + taskList.listId + "/" + task.taskId, subtask);
    }

    public VBox getTasks_vbox() {
        return tasks_vbox;
    }

    void stopDisplayingDialog() {
        mainCtrl.boardCtrl.stopDisplayingDialog(this);
    }
}
