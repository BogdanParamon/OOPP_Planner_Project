package client.scenes;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.ResourceBundle;

public class BoardCtrl implements Initializable {

    @FXML private VBox list1;

    /**
     * @param url    The location used to resolve relative paths for the root object, or
     *               {@code null} if the location is not known.
     * @param bundle The resources used to localize the root object, or {@code null} if
     *               the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle bundle) {
        list1.getChildren().add(new Task());
    }
}
