package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardCtrl implements Initializable {


    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private VBox list1;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public BoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * @param url    The location used to resolve relative paths for the root object, or
     *               {@code null} if the location is not known.
     * @param bundle The resources used to localize the root object, or {@code null} if
     *               the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle bundle) {
        list1.getChildren().add(new Task("ignore"));
    }

    public void switchToAddTask() {
        mainCtrl.showAddTask();
    }
}
