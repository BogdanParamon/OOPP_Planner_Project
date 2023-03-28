package client.scenes;

import client.utils.ServerUtils;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class List extends Pane {

    @FXML
    private VBox list;

    @FXML
    private MFXButton addButton;

    private TaskList taskList;

    @FXML private MFXTextField title;

    private ServerUtils server;
    private long boardId;


    public List() {
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

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            ArrayList<Object> listIdAndNewTitle = new ArrayList<>(2);
            listIdAndNewTitle.add(taskList.listId);
            listIdAndNewTitle.add(newValue);
            server.send("/app/taskLists/rename/" + boardId, listIdAndNewTitle);
        });


        addButton.setOnAction(event -> addTask(null, null));
        addButton.setText("");

        setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        setOnDragDropped(event -> {
            System.out.println("Drag dropped");
            Dragboard db = event.getDragboard();

            int sceneY =  (int) event.getSceneY() - 190;

            int length = list.getChildren().size();

            System.out.println(sceneY);

            addTask(db.getString(), (sceneY / 75) % length) ;

            event.setDropCompleted(true);
            event.consume();
        });
    }

    public void addTask(String title, Integer index) {
        int len = list.getChildren().size();

        Card card = new Card();
        if (title == null ) card.setText("Title " + len);

        else card.setText(title);
        list.getChildren().add(index == null ? len - 1 : index, card);

        VBox.setMargin(card, new Insets(5, 0,5, 5));

    }

    public void deleteCard(Card card) {
        list.getChildren().remove(card);
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
        title.setText(taskList.title);
        for (var task : taskList.tasks) {
            Card card = new Card();
            card.setText(task.title);
            list.getChildren().add(card);
        }
    }

    public void setServerUtils(ServerUtils server) {
        this.server = server;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTitle(String newTitle) {
        if (!newTitle.equals(title.getText())) {
            title.setText(newTitle);
        }
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
