package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.TaskList;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class BoardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Text boardName;
    @FXML
    private AnchorPane root;
    @FXML
    private HBox board_hbox;
    private Board board;
    @FXML
    private TextField newtTitle;

    @FXML
    private Button editTitle;
    @FXML
    private Button save;


    /**
     * Setup server and main controller
     *
     * @param server   server to connect to
     * @param mainCtrl the main controller - for switching scenes
     */
    @Inject
    public BoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * @param url    The location used to resolve relative paths for the root object, or
     *               {@code null} if the location is not known.
     * @param bundle The resources used to localize the root object, or {@code null} if
     *               the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle bundle) {
        mainCtrl.initHeader(root);
    }



    public void switchToAddTask() {

        mainCtrl.showAddTask();
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchToBoardOverviewScene() {
        mainCtrl.showBoardOverview();
    }

    public void setBoard(Board board) {
        this.board = board;
        boardName.setText(board.title);
        board_hbox.getChildren().clear();
        for (var taskList : board.lists) {
            List list = new List();
            list.setServerUtils(server);
            list.setTaskList(taskList);
            board_hbox.getChildren().add(list);
        }
    }

    public void addList() {
        TaskList list = new TaskList("New List");
        list = server.addList(list, board.boardId);
        List listUI = new List();
        listUI.setServerUtils(server);
        listUI.setTaskList(list);
        board_hbox.getChildren().add(listUI);
    }

    public void updateTitle() {
        newtTitle.setVisible(true);
        boardName.setVisible(false);
        editTitle.setVisible(false);
        save.setVisible(true);
    }

    public void saveNewTitle() {
        String newTitleS = newtTitle.getText().trim();
        newtTitle.setVisible(false);
        boardName.setVisible(true);
        editTitle.setVisible(true);
        save.setVisible(false);
        if (!newTitleS.isEmpty()) {
            this.board.title = newTitleS;
            setBoard(board);
            updateBoard(this.board);
        }
        newtTitle.setText("");
    }

    public void updateBoard(Board board) {
        try {
            server.send("/app/boards", board);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
