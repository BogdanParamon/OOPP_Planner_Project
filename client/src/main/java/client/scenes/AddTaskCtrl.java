package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Task;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

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

    public void ok() {
        try {
            server.addTask(getTask());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearField();
        switchToBoard();
    }

    private Task getTask() {
        String title = this.title.getText();
        System.out.println(title);
        return new Task(title);
    }

    public void switchToBoard() {
        mainCtrl.showBoard();
    }

    public void clearField() {
        title.clear();
    }

}
