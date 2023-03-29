package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import io.github.palexdev.materialfx.controls.MFXButton;
import commons.TaskList;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final TaskList taskList;
    private final Task task;
    @FXML
    private MFXButton deleteTaskButton;

    @FXML private TextField taskTitle;


    /**
     * New Card component
     * @param mainCtrl
     * @param server
     * @param task
     * @param taskList
    @FXML private TextField taskTitle;
     */
    public Card(MainCtrl mainCtrl, ServerUtils server, Task task, TaskList taskList) {

        this.mainCtrl = mainCtrl;
        this.server = server;
        this.task = task;
        this.taskList = taskList;

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

        deleteTaskButton.setOnAction(event -> {
            ((VBox) getParent()).getChildren().remove(this);
            server.deleteTask(this.task);
            taskList.tasks.remove(this.task);
        });

        taskTitle.setText(task.title);

        initDrag();
        initEditTaskTitle();

        URL cssURL = getClass().getResource("/client/scenes/Components/Cardstyle.css");
        if (cssURL != null) {
            String cssPath = cssURL.toExternalForm();
            getStylesheets().add(cssPath);
        } else {
            System.out.println("Can not load Cardstyle.css");
        }
    }


    void initDrag() {

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.taskId));
            db.setContent(content);
            event.consume();
        });

        setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                taskList.tasks.remove(task);
                server.updateList(taskList);
                ((VBox) getParent()).getChildren().remove(this);
            }
            event.consume();
        });
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

    private void handleFocusChange(ObservableValue<? extends Boolean>
                                           observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            saveTaskTitle();
        }
    }

    private void saveTaskTitle() {
        task.title = taskTitle.getText();
        server.updateTask(task);
    }

}
