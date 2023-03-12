package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class AddTaskCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    @FXML
    private MFXTextField title;

    @Inject
    public AddTaskCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }


}
