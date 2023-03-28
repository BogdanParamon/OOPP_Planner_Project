package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import commons.TaskList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final TaskList taskList;
    private final Task task;

    @FXML private TextField taskTitle;

    /**
     * Constructs a new Card instance with the specified parameters.
     * @param mainCtrl  The MainCtrl instance that manages the main application view.
     * @param server    The ServerUtils instance for handling server communication.
     * @param task      The Task instance representing the task to be displayed in the card.
     * @param taskList  The TaskList instance containing the task.
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

        taskTitle.setText(task.title);
        initDrag();
        initEditTaskTitle();
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

    void initEditTaskTitle(){
        taskTitle.setOnKeyReleased(event -> handleKeyRelease(event));
    }

    private void handleKeyRelease(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            taskTitle.getParent().requestFocus();
            task.title = taskTitle.getText();
            server.updateTask(task);
        }
    }
}
