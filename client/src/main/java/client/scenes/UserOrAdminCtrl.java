package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserOrAdminCtrl implements Initializable {
    private ServerUtils server;
    private MainCtrl mainCtrl;

    @FXML
    private AnchorPane root;

    @FXML
    private MFXTextField nameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton button;

    private boolean mode; // 0 -> user, 1 -> admin

    @Inject
    public UserOrAdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCtrl.initHeader(root);
        nameField.setVisible(true);
        passwordField.setVisible(false);
        mode = false;
    }

    public void view() {
        if (mode == false) {
            mode = true;
            nameField.setVisible(false);
            passwordField.setVisible(true);
            button.setText("User view");
        } else {
            mode = false;
            nameField.setVisible(true);
            passwordField.setVisible(false);
            button.setText("Admin view");
        }
    }

    public void ok() {
        mainCtrl.showBoardOverview();
    }

}
