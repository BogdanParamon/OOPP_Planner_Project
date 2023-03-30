package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
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

import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final Board board;
    private final TaskList taskList;
    private final Task task;
    @FXML
    private MFXButton deleteTaskButton;

    @FXML
    private TextField taskTitle;

    @FXML
    private MFXButton openTask;

    /**
     * Constructs a new Card instance with the specified parameters.
     *
     * @param mainCtrl  The MainCtrl instance that manages the main application view.
     * @param server    The ServerUtils instance for handling server communication.
     * @param task      The Task instance representing the task to be displayed in the card.
     * @param taskList  The TaskList instance containing the task.
     * @param board     The Board instance containing the taskList.
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

        deleteTaskButton.setOnAction(event -> {
            server.send("/app/tasks/delete/" + board.boardId + "/" + taskList.listId, task.taskId);
        });

        taskTitle.setText(task.title);

        initDrag();
        initEditTaskTitle();

        openTask.setOnAction(event -> displayDialog());

        URL cssURL = getClass().getResource("/client/scenes/Components/Cardstyle.css");
        if (cssURL != null) {
            String cssPath = cssURL.toExternalForm();
            getStylesheets().add(cssPath);
        } else {
            System.out.println("Can not load Cardstyle.css");
        }
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

            mainCtrl.boardCtrl.getRoot().getChildren().add(this); // find a better way for this
            setVisible(false);

            db.setContent(content);
            event.consume();
        });

        setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                if (!event.getDragboard().getString().equals("Same list"))
                    taskList.tasks.remove(task);
                server.updateList(taskList);
            } else {
                orignalParent[0].getChildren().add(index[0], this);
                setVisible(true);
            }
            mainCtrl.boardCtrl.getRoot().getChildren().remove(this);
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

    public Task getTask() {
        return task;
    }

    public TextField getTaskTitle() {
        return taskTitle;
    }
}
