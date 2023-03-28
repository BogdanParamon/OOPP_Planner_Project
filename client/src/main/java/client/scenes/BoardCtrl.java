package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.TaskList;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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
    @FXML
    private Pane customize;
    @FXML
    private ColorPicker colorPickerBackground;
    @FXML
    private Text txtCust;
    @FXML
    private ColorPicker colorPickerBoard;
    @FXML
    private ScrollPane boardScrollPane;
    @FXML
    private ColorPicker colorPickerButtons;
    @FXML
    private VBox addListTaskVBox;
    @FXML
    private MFXButton addList;
    @FXML
    private MFXButton addTask;
    @FXML
    private Pane custimozePane;
    @FXML
    private Pane overviewBoardsPane;
    @FXML
    private MFXButton btnCustomize;
    @FXML
    private MFXButton btnOverviewBoards;

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
        customize.setVisible(false);
        mainCtrl.showBoardOverview();
    }

    public void setBoard(Board board) {
        this.board = board;
        boardName.setText(board.title);
        board_hbox.getChildren().clear();
        for (var taskList : board.lists) {
            List list = new List(mainCtrl, server, taskList);
            board_hbox.getChildren().add(list);
        }
        root.setStyle("-fx-background-color: #" + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #" + board.backgroundColor + ";");
        save.setStyle("-fx-background-color: #" + board.backgroundColor + ";");
        addListTaskVBox.setStyle("-fx-background-color: #"
                + board.buttonsBackground + "; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        addTask.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        btnCustomize.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        btnOverviewBoards.setStyle("-fx-background-color: #" + board.buttonsBackground + ";");
        overviewBoardsPane.setStyle("-fx-background-color: #"
                + board.buttonsBackground + ";-fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: #"
                + board.buttonsBackground + ";-fx-background-radius: 10px;");
    }

    public void addList() {
        TaskList list = new TaskList("New List");
        list = server.addList(list, board.boardId);
        List listUI = new List(mainCtrl, server, list);
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
            boardName.setText(this.board.title);
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

    public void showCustomize() {
        if (customize.isVisible()) customize.setVisible(false);
        else customize.setVisible(true);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        colorPickerButtons.setValue(Color.valueOf(board.buttonsBackground));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void closeCustomize() {
        customize.setVisible(false);
    }

    public void applyChanges() {
        //background color
        String rootColor = colorPickerBackground.getValue().toString().substring(2, 8);
        root.setStyle("-fx-background-color: #" + rootColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #" + rootColor + ";");
        save.setStyle("-fx-background-color: #" + rootColor + ";");
        this.board.backgroundColor = rootColor;
        txtCust.setFill(Paint.valueOf(rootColor));
        //board color
        String boardColor = colorPickerBoard.getValue().toString().substring(2, 8);
        boardScrollPane.setStyle("-fx-background-color: #"
                + boardColor + "; -fx-background-radius: 10px;");
        //button color
        String buttonColor = colorPickerButtons.getValue().toString().substring(2, 8);
        addListTaskVBox.setStyle("-fx-background-color: #"
                + buttonColor + "; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: #" + buttonColor + ";");
        addTask.setStyle("-fx-background-color: #" + buttonColor + ";");
        btnCustomize.setStyle("-fx-background-color: #" + buttonColor + ";");
        btnOverviewBoards.setStyle("-fx-background-color: #" + buttonColor + ";");
        overviewBoardsPane.setStyle("-fx-background-color: #"
                + buttonColor + ";-fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: #"
                + buttonColor + ";-fx-background-radius: 10px;");
        this.board.buttonsBackground = buttonColor;

        updateBoard(board);
    }

    public void resetBackgroundColor() {
        this.board.backgroundColor = "ffffff";
        root.setStyle("-fx-background-color: #" + board.backgroundColor
                + "; -fx-border-color: black; -fx-border-width: 2px;");
        editTitle.setStyle("-fx-background-color: #ffffff;");
        save.setStyle("-fx-background-color: #ffffff;");
        updateBoard(board);
        colorPickerBackground.setValue(Color.valueOf(board.backgroundColor));
        txtCust.setFill(Paint.valueOf(board.backgroundColor));
    }

    public void resetBoardColor() {
        boardScrollPane.setStyle("-fx-background-color: #ddd; -fx-background-radius: 10px;");

        colorPickerBoard.setValue(Color.valueOf("ddd"));
    }

    public void resetButtonColor() {
        this.board.buttonsBackground = "ddd";
        addListTaskVBox.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        addList.setStyle("-fx-background-color: ddd;");
        addTask.setStyle("-fx-background-color: ddd;");
        btnOverviewBoards.setStyle("-fx-background-color: ddd;");
        btnCustomize.setStyle("-fx-background-color: ddd;");
        overviewBoardsPane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");
        custimozePane.setStyle("-fx-background-color: ddd; -fx-background-radius: 10px;");

        updateBoard(board);
        colorPickerButtons.setValue(Color.valueOf("ddd"));
    }
}
