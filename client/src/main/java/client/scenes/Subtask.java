package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;

public class Subtask extends Pane {

    private ServerUtils server;
    private MainCtrl mainCtrl;
    private Task task;
    private commons.Subtask subtask;

    @FXML
    private MFXButton editButton;
    @FXML
    private MFXButton openTask;
    @FXML
    private Checkbox checkbox;

    public Subtask(MainCtrl mainCtrl, ServerUtils server, Task task, commons.Subtask subtask) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.task = task;
        this.subtask = subtask;

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/Subtask.fxml"));
        loader.setRoot(this);
        loader.setController(Subtask.this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }


    }


}
