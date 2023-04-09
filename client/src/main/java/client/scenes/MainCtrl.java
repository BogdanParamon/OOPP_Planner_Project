/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    protected Stage primaryStage;
    protected BoardOverviewCtrl boardOverviewCtrl;
    protected Scene boardOverview;
    protected HomeCtrl homeCtrl;
    protected Scene home;
    protected BoardCtrl boardCtrl;
    protected Scene board;
    protected AddTaskCtrl addTaskCtrl;
    protected Scene addTask;
    protected Scene detailedTask;

    protected Scene userOrAdmin;
    protected UserOrAdminCtrl userOrAdminCtrl;
    protected AdminOverviewCtrl adminOverviewCtrl;
    protected Scene adminOverview;

    protected PasswordCtrl passwordCtrl;
    protected Scene password;

    private double xOffset, yOffset;

    /**
     * Initialize all controllers and scenes
     *
     * @param primaryStage  main stage for application
     * @param home          first scene for connecting to a server
     * @param boardOverview home scene for a server
     * @param board         board scene with the lists and tasks
     * @param addTask       add task scene - allows user to create a new task with a title
     * @param userOrAdmin   allow user to pick between user view and admin view
     * @param adminOverview board overview with admin privileges
     * @param password      requests password for joining a board
     */
    public void initialize(Stage primaryStage,
                           Pair<HomeCtrl, Parent> home,
                           Pair<BoardOverviewCtrl, Parent> boardOverview,
                           Pair<BoardCtrl, Parent> board,
                           Pair<AddTaskCtrl, Parent> addTask,
                           Pair<UserOrAdminCtrl, Parent> userOrAdmin,
                           Pair<AdminOverviewCtrl, Parent> adminOverview,
                           Pair<PasswordCtrl, Parent> password) {
        this.primaryStage = primaryStage;

        this.homeCtrl = home.getKey();
        this.home = new Scene(home.getValue());

        this.boardOverviewCtrl = boardOverview.getKey();
        this.boardOverview = new Scene(boardOverview.getValue());

        this.boardCtrl = board.getKey();
        this.board = new Scene(board.getValue());

        this.addTaskCtrl = addTask.getKey();
        this.addTask = new Scene(addTask.getValue());

        this.userOrAdminCtrl = userOrAdmin.getKey();
        this.userOrAdmin = new Scene(userOrAdmin.getValue());

        this.adminOverviewCtrl = adminOverview.getKey();
        this.adminOverview = new Scene(adminOverview.getValue());

        this.passwordCtrl = password.getKey();
        this.password = new Scene(password.getValue());

        primaryStage.setScene(this.home);
        primaryStage.show();
    }

    /**
     * board
     * Change scene to home
     */
    public void showHome() {
        primaryStage.setTitle("Home");
        primaryStage.setScene(home);
    }

    /**
     * Change scene to board overview
     */
    public void showBoardOverview() {
        primaryStage.setTitle("Board Overview");
        primaryStage.setScene(boardOverview);
        boardOverviewCtrl.load();
    }

    /**
     * Change scene to board
     */
    public void showBoard() {
        primaryStage.setTitle("Board");
        primaryStage.setScene(board);
    }

    public void showAddTask() {
        primaryStage.setTitle("Add Task");
        primaryStage.setScene(addTask);
    }

    public void showUserOrAdmin() {
        primaryStage.setTitle("Select mode");
        primaryStage.setScene(userOrAdmin);
    }

    public void showAdminOverview() {
        primaryStage.setTitle("Admin Board Overview");
        primaryStage.setScene(adminOverview);
        adminOverviewCtrl.load();
    }

    public void showPassword() {
        primaryStage.setTitle("Password");
        primaryStage.setScene(password);
    }

    /**
     * Getter for the board
     *
     * @return boardCtrl
     */
    public BoardCtrl getBoard() {
        return boardCtrl;
    }

    public void showDetailedTask() {
        primaryStage.setTitle("Task Details");
        primaryStage.setScene(detailedTask);
    }

    public void initHeader(AnchorPane root) {

        HBox header = new HBox();
        header.setPrefSize(root.getPrefWidth(), 25);
        header.setSpacing(15);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(5, 10, 0, 0));

        MFXFontIcon closeIcon = new MFXFontIcon();
        closeIcon.setDescription("mfx-circle");
        closeIcon.setSize(15);
        closeIcon.setId("closeIcon");

        MFXFontIcon minimizeIcon = new MFXFontIcon();
        minimizeIcon.setDescription("mfx-circle");
        minimizeIcon.setSize(15);
        minimizeIcon.setId("minimizeIcon");


        header.getChildren().add(minimizeIcon);
        header.getChildren().add(closeIcon);


        root.getChildren().add(root.getChildren().size() - 1, header);

        closeIcon.setOnMouseClicked(event -> Platform.exit());
        minimizeIcon.setOnMouseClicked(event -> primaryStage.setIconified(true));

        header.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        header.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
    }
}