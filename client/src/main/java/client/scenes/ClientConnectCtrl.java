package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class ClientConnectCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public ClientConnectCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void switchSceneToHome() {
        mainCtrl.showHome();
    }

}
