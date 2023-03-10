package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Card extends Pane {
    @FXML private Text title;

    private String text = "";

    /**
     * New component Card
     */
    public Card() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
        loader.setRoot(this);
        loader.setController(Card.this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
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

}
