package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class UserOrAdminCtrl implements Initializable {
    private ServerUtils server;
    private MainCtrl mainCtrl;

    @FXML
    private AnchorPane root;

    @FXML
    private MFXButton button;
    @FXML
    private MFXTextField nameField;

    @FXML
    private MFXButton viewButton;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private Text incorrectPasswordMsg;

    private boolean adminMode; // 0 -> user, 1 -> admin

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
        adminMode = false;
        button.setOnAction(event -> confirm());
    }


    public void view() {
        incorrectPasswordMsg.setVisible(false);
        mainCtrl.userOrAdmin.getStylesheets().remove("/client/styles/inputerror.css");
        if (!adminMode) {
            adminMode = true;
            nameField.setVisible(false);
            passwordField.setVisible(true);
            viewButton.setText("User view");
        } else {
            adminMode = false;
            nameField.setVisible(true);
            passwordField.setVisible(false);
            viewButton.setText("Admin view");
        }
    }

    public void confirm() {
        if (!adminMode) {
            switchSceneToBoardOverview(server.connectToUser(nameField.getText()));
        } else {
            if (!passwordField.getText().isEmpty()
                    && server.verifyAdminPassword(passwordField.getText())) {
                switchSceneToAdminOverview();
            } else {
                mainCtrl.userOrAdmin.getStylesheets().add("/client/styles/inputerror.css");
                incorrectPasswordMsg.setVisible(true);
                passwordField.clear();
            }
        }
    }

    public void switchSceneToBoardOverview(User user) {
        mainCtrl.boardOverviewCtrl.setUser(user);
        mainCtrl.showBoardOverview();
    }

    public void switchSceneToHome() {
        mainCtrl.showHome();
    }

    public void switchSceneToAdminOverview() {
        incorrectPasswordMsg.setVisible(false);
        mainCtrl.userOrAdmin.getStylesheets().remove("/client/styles/inputerror.css");
        mainCtrl.showAdminOverview();
    }
}
