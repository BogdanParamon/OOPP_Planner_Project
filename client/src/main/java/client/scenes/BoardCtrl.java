package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.TaskList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

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

    private ArrayList<List> lists;
    private Set<StompSession.Subscription> subscriptions;


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

    public StompSession.Subscription registerForNewLists() {
        return server.registerForMessages("/topic/taskLists/add/" + board.boardId, TaskList.class,
                taskList -> Platform.runLater(() -> {
                    List list = new List();
                    list.setServerUtils(server);
                    list.setTaskList(taskList);
                    list.setBoardId(board.boardId);
                    board_hbox.getChildren().add(list);
                }));
    }

    public StompSession.Subscription registerForListRenames() {
        return server.registerForMessages("/topic/taskLists/rename/" + board.boardId,
                ArrayList.class, listIdAndNewTitle -> Platform.runLater(() -> {
                    var listId = listIdAndNewTitle.get(0);
                    listId = (long) (int) listId;
                    String newTitle = (String) listIdAndNewTitle.get(1);
                    for (Node node : board_hbox.getChildren()) {
                        List list = (List) node;
                        if (list.getTaskList().listId == (Long) listId) {
                            list.setTitle(newTitle);
                            break;
                        }
                    }
                }));
    }

    public void switchToAddTask() {

        mainCtrl.showAddTask();
    }

    /**
     * Uses showHome method to switch scenes to Home scene
     */
    public void switchToBoardOverviewScene() {
        subscriptions.forEach(StompSession.Subscription::unsubscribe);
        mainCtrl.showBoardOverview();
    }

    public void setBoard(Board board) {
        this.board = board;
        boardName.setText(board.title);
        board_hbox.getChildren().clear();
        subscriptions = new HashSet<>();
        for (var taskList : board.lists) {
            List list = new List();
            list.setServerUtils(server);
            list.setTaskList(taskList);
            list.setBoardId(board.boardId);
            board_hbox.getChildren().add(list);
        }
        subscriptions.add(registerForNewLists());
        subscriptions.add(registerForListRenames());
    }

    public void addList() {
        TaskList list = new TaskList("New List");
        server.send("/app/taskLists/add/" + board.boardId, list);
    }
}
