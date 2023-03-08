package client.scenes;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeCtrl implements Initializable {
    @FXML private MFXListView<String> boards;

    @FXML private Text subheading;

    public void initialize(URL url, ResourceBundle bundle) {
        boards.setItems(FXCollections.observableArrayList("a","b","c","d","e","f","h","a","b","c","d","e","f","h","a","b","c","d","e","f","h"));
    }


}
