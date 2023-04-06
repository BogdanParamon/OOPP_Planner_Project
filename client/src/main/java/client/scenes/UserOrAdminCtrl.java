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
        button.setOnAction(
                event -> switchSceneToBoardOverview(server.connectToUser(nameField.getText())));
    }


    public void view() {
        if (mode == false) {
            mode = true;
            nameField.setVisible(false);
            passwordField.setVisible(true);
            viewButton.setText("User view");
        } else {
            mode = false;
            nameField.setVisible(true);
            passwordField.setVisible(false);
            viewButton.setText("Admin view");
        }
    }

    public void switchSceneToBoardOverview(User user) {
        mainCtrl.boardOverviewCtrl.setUser(user);
        mainCtrl.boardOverviewCtrl.registerForNewBoards();
        mainCtrl.showBoardOverview();
    }

}
