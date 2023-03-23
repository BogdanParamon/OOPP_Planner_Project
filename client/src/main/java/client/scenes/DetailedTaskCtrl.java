package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class DetailedTaskCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    /**
     * Setup server and main controller
     * @param server server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public DetailedTaskCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }



}
