package client.scenes;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class List extends Pane {

    @FXML
    private VBox list;

    @FXML
    private MFXButton addButton;


    public List() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Components/List.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        addButton.setOnAction(event -> addTask());
        addButton.setText("");
    }

    public void addTask() {
        int len = list.getChildren().size();
        Task task = new Task();
        list.getChildren().add(len - 1, task);
        VBox.setMargin(task, new Insets(5, 0,5, 5));

    }

    public void deleteCard(Task task) {
        list.getChildren().remove(task);
    }

}
