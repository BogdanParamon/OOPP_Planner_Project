package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class Card extends Pane {

    private final MainCtrl mainCtrl;
    private ServerUtils server;

    private Task task;

    @FXML private Text title;


    public Card(MainCtrl mainCtrl, ServerUtils server, Task task) {

        this.mainCtrl = mainCtrl;
        this.server = server;
        this.task = task;

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

        title.setText(task.title);

        init();
    }

    void init() {

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(task.title);
            db.setContent(content);
            event.consume();
        });

        setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                ((VBox) getParent()).getChildren().remove(this);
            }
            event.consume();
        });
    }
}
