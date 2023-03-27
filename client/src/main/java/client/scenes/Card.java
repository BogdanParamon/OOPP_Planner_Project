package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import io.github.palexdev.materialfx.controls.MFXButton;
import commons.TaskList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class Card extends Pane {
//<<<<<<< HEAD
//
//    @FXML
//    private Text title;
//
//    private ServerUtils server;
//
//    private String text = "";
//
//    @FXML
//    private MFXButton deleteTaskButton;
//
//    private Task task;
//
//    /**
//     * New component Card
//     */
//    public Card() {
//
//=======

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final TaskList taskList;
    private final Task task;

    @FXML
    private MFXButton deleteTaskButton;

    @FXML private Text title;


    /**
     * New Card component
     * @param mainCtrl
     * @param server
     * @param task
     * @param taskList
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
        });

        title.setText(task.title);
        initDrag();
    }


    public void deleteTask(VBox list) {
        deleteTaskButton.setOnAction(event -> {
            list.getChildren().remove(this);
            server.deleteTask(task);
        });
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
}
