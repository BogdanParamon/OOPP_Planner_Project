package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ShortcutsCtrl implements Initializable {

    @FXML
    private VBox shortcutsBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showShortcuts() {
        shortcutsBox.setVisible(true);
    }

    public void hideShortcuts() {
        shortcutsBox.setVisible(false);
    }

    public boolean isShortcutsVisible() {
        return shortcutsBox.isVisible();
    }


}
