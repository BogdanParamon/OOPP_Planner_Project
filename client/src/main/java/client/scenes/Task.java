package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public class Task extends Pane {
    @FXML
    private Text title;

    private String text = "";

    /**
     * New component Card
     */
    public Task() {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/Task.fxml"));
        loader.setRoot(this);
        loader.setController(Task.this);

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
