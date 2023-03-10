package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class ClientConnectCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * Setup server and main controller
     * @param server server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public ClientConnectCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchSceneToHome() {
        mainCtrl.showHome();
    }

}
