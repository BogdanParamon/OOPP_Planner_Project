package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ClientConnectCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField serverPath;


    /**
     * Setup server and main controller
     *
     * @param server   server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public ClientConnectCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * connects to entered server
     */
    public void connect() {
        ServerUtils.setSERVER(serverPath.getText());
        switchSceneToHome();
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchSceneToHome() {
        mainCtrl.showHome();
    }

}
