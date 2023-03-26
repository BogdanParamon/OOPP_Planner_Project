package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import io.github.palexdev.materialfx.controls.MFXButton;
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

    @FXML
    private Text title;

    private ServerUtils server;

    private String text = "";

    @FXML
    private MFXButton deleteTaskButton;

    private Task task;

    /**
     * New component Card
     */
    public Card() {

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

        init();
    }


    public void deleteTask(VBox list){
        deleteTaskButton.setOnAction(event -> {
            list.getChildren().remove(this);
            server.deleteTask(task);
        });
    }

    /**
     * @return title text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text title to change
     */
    public void setText(String text) {
        this.text = text;
        title.setText(text);
    }



    void init() {

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(text); // TODO pass this
            db.setContent(content);
            event.consume();
        });

        setOnDragDone(event -> {
            System.out.println("Drag done");

            // TODO not remove when invalid drop
            ((VBox) getParent()).getChildren().remove(this);
            event.consume();
        });

    }


}
