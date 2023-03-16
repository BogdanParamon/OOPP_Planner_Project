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
     * @param title title of the task
     * New component Card
     */
    public Task(String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Task.fxml"));
        loader.setRoot(this);
        loader.setController(Task.this);
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        this.title.setText(title);
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
