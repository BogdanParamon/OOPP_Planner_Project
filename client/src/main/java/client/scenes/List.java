package client.scenes;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class List extends Pane {

    @FXML
    private VBox list;

    @FXML
    private MFXButton addButton;


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

}
