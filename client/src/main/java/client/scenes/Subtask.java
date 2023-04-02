package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.awt.*;
import java.io.IOException;

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
    private CheckBox checkbox;

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

        checkbox.setText("New Subtask");
        inputTitle.setText("");
        editButton.setOnAction(event -> updateSubtaskTitle());
        saveButton.setOnAction(event -> saveSubtaskTitle());

        deleteButton.setOnAction(event -> {
            server.send("/app/subtasks/delete/" + task.taskId, subtask.subTaskId);

        });
    }

    public void updateSubtaskTitle() {
        inputTitle.setVisible(true);
        checkbox.setText("");
        editButton.setVisible(false);
        saveButton.setVisible(true);
    }

    public void saveSubtaskTitle() {
        String updatedSubtaskText = inputTitle.getText().trim();
        if (!(inputTitle.getText().trim()).isEmpty()) {
            inputTitle.setVisible(false);
            renameSubtask(updatedSubtaskText);
            checkbox.setText(inputTitle.getText().trim());
            inputTitle.setText("");
            editButton.setVisible(true);
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
            server.send("/app/subtasks/rename/" + subtask.subTaskId, text);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
