package client.scenes;

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

    private String text = "";

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

        init();
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
