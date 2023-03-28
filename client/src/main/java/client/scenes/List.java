package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class List extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final TaskList taskList;
    private Integer dragIndex;

    @FXML
    private VBox list;
    @FXML
    private MFXButton addButton;

    @FXML
    private MFXTextField title;
    @FXML
    private MFXButton deleteTaskListButton;

    private Board board;

    public List(MainCtrl mainCtrl, ServerUtils server, TaskList taskList, Board board) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.taskList = taskList;

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/List.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        for (Task task : taskList.tasks) {
            Card card = new Card(mainCtrl, server, task, taskList);
            list.getChildren().add(0, card);
        }

        title.setText(taskList.title);
        title.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            ArrayList<Object> listIdAndNewTitle = new ArrayList<>(2);
            listIdAndNewTitle.add(taskList.listId);
            listIdAndNewTitle.add(newValue);
            server.send("/app/taskLists/rename/" + board.boardId, listIdAndNewTitle);
        });

        addButton.setOnAction(event -> addTask());
        addButton.setText("");
        dragIndex = null;

        deleteTaskListButton.setOnAction(event -> {
            ((HBox) getParent()).getChildren().remove(this);
            server.deleteTaskList(taskList);
            board.lists.remove(this.taskList);
        });

        initDrag(mainCtrl, server);
    }

    private void initDrag(MainCtrl mainCtrl, ServerUtils server) {
        setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            int index = getIndex(event);

            if (!Objects.equals(index, dragIndex)) {
                if (dragIndex != null) {
                    list.getChildren().remove(list.getChildren().get(dragIndex));
                }
                dragIndex = index;

                Card card = new Card(mainCtrl, server, new Task(""), null);
                card.setStyle(card.getStyle().replace("ddd", "#43b2e6"));
                list.getChildren().add(dragIndex, card);
            }

            event.consume();
        });

        setOnDragExited(event -> {
            if (dragIndex != null) {
                list.getChildren().remove(list.getChildren().get(dragIndex));
                dragIndex = null;
            }
        });

        setOnDragDropped(event -> {
            System.out.println("Drag dropped");
            Dragboard db = event.getDragboard();

            int index = getIndex(event);
            list.getChildren().remove(list.getChildren().get(dragIndex));
            addTask(Long.parseLong(db.getString()), index);
            dragIndex = null;

            event.setDropCompleted(true);
            event.consume();
        });
    }


    private int getIndex(DragEvent event) {
        int sceneY = (int) event.getSceneY() - 190;
        int length = list.getChildren().size();
        int index = length != 1 ? (sceneY / 75) % (length - 1) : 0;
        if (sceneY >= 85 * (length - 1)) index = Integer.max(0, length - 2);
        return index;
    }

    public void addTask() {
        Task task = server.addTask(new Task("Title"), taskList.listId);
        taskList.tasks.add(0, task);
        Card card = new Card(mainCtrl, server, task, taskList);
        list.getChildren().add(0, card);
    }

    public void addTask(long taskId, Integer index) {
        int len = list.getChildren().size();

        Task task = server.getTaskById(taskId);
        taskList.tasks.add(index, task);
        server.updateList(taskList);
        Card card = new Card(mainCtrl, server, task, taskList);
        list.getChildren().add(index, card);

        VBox.setMargin(card, new Insets(5, 0, 5, 5));

    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTitle(String newTitle) {
        if (!newTitle.equals(title.getText())) {
            title.setText(newTitle);
        }
    }
}
