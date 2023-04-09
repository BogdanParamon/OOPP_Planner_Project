package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Collections;

public class Subtask extends AnchorPane {

    private ServerUtils server;
    private MainCtrl mainCtrl;
    private final Board board;
    private final TaskList taskList;
    private final Task task;
    private final commons.Subtask subtask;

    @FXML
    private TextField inputTitle;
    @FXML
    private MFXButton editButton;
    @FXML
    private MFXButton saveButton;
    @FXML
    private MFXButton deleteButton;
    @FXML
    private MFXCheckbox checkbox;
    @FXML
    private MFXButton arrowUp;
    @FXML
    private MFXButton arrowDown;

    public Subtask(MainCtrl mainCtrl, ServerUtils server, Board board, TaskList taskList,
                   Task task, commons.Subtask subtask) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.board = board;
        this.taskList = taskList;
        this.task = task;
        this.subtask = subtask;


        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/Subtask.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        checkbox.setText(subtask.subtaskText);
        inputTitle.setText("");
        editButton.setOnAction(event -> updateSubtaskTitle());
        saveButton.setOnAction(event -> saveSubtaskTitle());

        deleteButton.setOnAction(event -> {

            server.send("/app/subtasks/delete/" + board.boardId + "/"
                    + taskList.listId + "/" + task.taskId, subtask.subTaskId);

        });

        checkbox.setOnAction(event -> {
            if (checkbox.isSelected()) {
                subtask.subtaskBoolean = true;
                server.send("/app/subtasks/status/" + board.boardId + "/"
                        + taskList.listId + "/" + task.taskId, subtask);
            } else {
                subtask.subtaskBoolean = false;
                server.send("/app/subtasks/status/" + board.boardId + "/"
                        + taskList.listId + "/" + task.taskId, subtask);
            }
        });

        arrowDown.setOnAction(event -> arrowDownFct());

        arrowUp.setOnAction(event -> arrowUpFct());

        inputTitle.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER && inputTitle.isVisible()) saveSubtaskTitle();
        });

    }

    public void updateSubtaskTitle() {
        inputTitle.setVisible(true);
        checkbox.setText("");
        editButton.setVisible(false);
        arrowUp.setVisible(false);
        arrowDown.setVisible(false);
        saveButton.setVisible(true);
        inputTitle.requestFocus();
    }

    public void saveSubtaskTitle() {
        String updatedSubtaskText = inputTitle.getText().trim();
        if (!updatedSubtaskText.isEmpty()) {
            inputTitle.setVisible(false);
            renameSubtask(updatedSubtaskText);
            inputTitle.setText("");
            editButton.setVisible(true);
            arrowUp.setVisible(true);
            arrowDown.setVisible(true);
            saveButton.setVisible(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please enter valid text.");
            alert.showAndWait();
            inputTitle.setText("Add text");
        }
    }

    public void renameSubtask(String text) {
        try {
            subtask.setSubtaskText(text);
            server.send("/app/subtasks/rename/" +  board.boardId + "/"
                    + taskList.listId + "/" + task.taskId, subtask);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public commons.Subtask getSubtask() {
        return subtask;
    }

    public MFXCheckbox getCheckbox() {
        return checkbox;
    }

    public void arrowDownFct() {
        VBox parent = (VBox) this.getParent();
        int index = parent.getChildren().indexOf(this);
        if (index < parent.getChildren().size() - 2) {
            Node first = parent.getChildren().get(index + 1);
            Node second = parent.getChildren().get(index);
            parent.getChildren().set(index + 1, new Text());
            parent.getChildren().set(index, new Text());
            parent.getChildren().set(index + 1, second);
            parent.getChildren().set(index, first);
            Collections.swap(task.subtasks, index + 1, index);
            server.send("/app/tasks/update/" + board.boardId + "/" + taskList.listId, task);
        }
    }

    public void arrowUpFct() {
        VBox parent = (VBox) this.getParent();
        int index = parent.getChildren().indexOf(this);
        if (index > 0) {
            Node first = parent.getChildren().get(index - 1);
            Node second = parent.getChildren().get(index);
            parent.getChildren().set(index - 1, new Text());
            parent.getChildren().set(index, new Text());
            parent.getChildren().set(index - 1, second);
            parent.getChildren().set(index, first);
            Collections.swap(task.subtasks, index - 1, index);
            server.send("/app/tasks/update/" + board.boardId + "/" + taskList.listId, task);
        }
    }
}
