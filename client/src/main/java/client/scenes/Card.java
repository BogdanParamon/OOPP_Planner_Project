package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.checkerframework.checker.units.qual.A;


import java.io.IOException;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final TaskList taskList;
    private final Task task;

    @FXML
    private TextField taskTitle;

    @FXML
    private MFXButton openTask;

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
        openTask.setOnAction(event -> displayDialog());
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
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            taskTitle.getParent().requestFocus();
            task.title = taskTitle.getText();
            server.updateTask(task);
        }
    }

    void displayDialog(){
//        Stage stage = new Stage();
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initStyle(StageStyle.DECORATED);
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Components/DetailedTask.fxml"));
//        AnchorPane root;
//
//        try {
//            root = loader.load();
//        } catch (IOException e){
//            throw new RuntimeException();
//        }
//
//        stage.setScene(new Scene(root));
//        stage.show();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        DetailedTask detailedTask = new DetailedTask(mainCtrl, server, task);

        AnchorPane root = detailedTask;
        stage.setScene(new Scene(root));
        stage.show();
    }
}
