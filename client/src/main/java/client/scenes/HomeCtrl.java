package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.util.Duration.millis;

public class HomeCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private MFXTextField serverPath;
    @FXML
    private Text errorMsg;

    @FXML
    private AnchorPane root;


    /**
     * Setup server and main controller
     *
     * @param server   server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public HomeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        mainCtrl.initHeader(root);
    }


    /**
     * connects to entered server
     */
    public void connect() {

        ServerUtils.setSERVER(serverPath.getText());
        if (server.validServer()) {
            ServerUtils.setSession(server.connectWebsocket());
            mainCtrl.boardOverviewCtrl.registerForNewBoards();

            switchSceneToBoardOverview();
            mainCtrl.home.getStylesheets().remove("/client/styles/inputerror.css");
            errorMsg.setVisible(false);
        } else {
            mainCtrl.home.getStylesheets().add("/client/styles/inputerror.css");
            errorMsg.setVisible(true);

            Timeline timeline = new Timeline(
                    new KeyFrame(millis(0), new KeyValue(serverPath.translateXProperty(), 0)),
                    new KeyFrame(millis(50), new KeyValue(serverPath.translateXProperty(), -10)),
                    new KeyFrame(millis(100), new KeyValue(serverPath.translateXProperty(), 10)),
                    new KeyFrame(millis(150), new KeyValue(serverPath.translateXProperty(), -10)),
                    new KeyFrame(millis(200), new KeyValue(serverPath.translateXProperty(), 0))
            );
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchSceneToBoardOverview() {
        mainCtrl.showBoardOverview();
    }

}
