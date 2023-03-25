package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class List extends Pane {

    @FXML private VBox list;
    @FXML private MFXButton addButton;

    private MainCtrl mainCtrl;
    private ServerUtils server;
    private TaskList taskList;

    private Integer dragIndex;

    public List(MainCtrl mainCtrl, ServerUtils server, TaskList taskList) {

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

        addButton.setOnAction(event -> addTask(null, null));
        addButton.setText("");
        dragIndex = null;

        setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            int sceneY =  (int) event.getSceneY() - 190;
            int length = list.getChildren().size();
            int index = length != 1 ? (sceneY / 75) % (length - 1) : 0;

            if (sceneY >= 85 * (length - 1)) index = Integer.max(0, length - 2);


            if (!Objects.equals(index,dragIndex)) {
                if (dragIndex != null) {
                    list.getChildren().remove(list.getChildren().get(dragIndex));
                    System.out.println("Removed");
                }
                dragIndex = index;
                System.out.println(dragIndex);

                Card card = new Card(mainCtrl, server, new Task(""));
                card.setStyle(card.getStyle().replace("ddd", "#43b2e6"));
                list.getChildren().add(dragIndex, card);
            }

            event.consume();
        });

        setOnDragExited(event -> {
            System.out.println("Drag exited");
            if (dragIndex != null) {
                list.getChildren().remove(list.getChildren().get(dragIndex));
                dragIndex = null;
            }
        });

        setOnDragDropped(event -> {
            System.out.println("Drag dropped");
            Dragboard db = event.getDragboard();

            int sceneY =  (int) event.getSceneY() - 190;
            int length = list.getChildren().size();
            int index = length != 1 ? (sceneY / 75) % (length - 1) : 0;
            if (sceneY >= 85 * (length - 1)) index = Integer.max(0, length - 2);
            list.getChildren().remove(list.getChildren().get(dragIndex));
            addTask(db.getString(), index) ;
            dragIndex = null;

            event.setDropCompleted(true);
            event.consume();
        });
    }

    public void addTask(String title, Integer index) {
        int len = list.getChildren().size();
        if (title == null) title = "Title " + len;

        Task task = new Task(title);
        Card card = new Card(mainCtrl, server, task);
        list.getChildren().add(index == null ? len - 1 : index, card);

        VBox.setMargin(card, new Insets(5, 0,5, 5));

    }

    public void deleteCard(Card card) {
        list.getChildren().remove(card);
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
        for (var task : taskList.tasks) {
            Card card = new Card(mainCtrl, server, task);
            list.getChildren().add(card);
        }
    }

}
