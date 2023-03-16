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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;
    private BoardOverviewCtrl boardOverviewCtrl;
    private Scene boardOverview;
    private HomeCtrl homeCtrl;
    private Scene home;
    private BoardCtrl boardCtrl;
    private Scene board;
    private AddTaskCtrl addTaskCtrl;
    private Scene addTask;

    private DetailedTaskCtrl detailedTaskCtrl;
    private Scene detailedTask;


    /**
     * Initialize all controllers and scenes
     *
     * @param primaryStage  main stage for application
     * @param home          first scene for connecting to a server
     * @param boardOverview home scene for a server
     * @param board         board scene with the lists and tasks
     * @param addTask       add task scene - allows user to create a new task with a title
     * @param detailedTask  allows user to see details of a task
     */
    public void initialize(Stage primaryStage,
                           Pair<HomeCtrl, Parent> home,
                           Pair<BoardOverviewCtrl, Parent> boardOverview,
                           Pair<BoardCtrl, Parent> board,
                           Pair<AddTaskCtrl, Parent> addTask,
                           Pair<DetailedTaskCtrl, Parent> detailedTask) {
        this.primaryStage = primaryStage;

        this.homeCtrl = home.getKey();
        this.home = new Scene(home.getValue());

        this.boardOverviewCtrl = boardOverview.getKey();
        this.boardOverview = new Scene(boardOverview.getValue());

        this.boardCtrl = board.getKey();
        this.board = new Scene(board.getValue());

        this.addTaskCtrl = addTask.getKey();
        this.addTask = new Scene(addTask.getValue());

        this.detailedTaskCtrl = detailedTask.getKey();
        this.detailedTask = new Scene(detailedTask.getValue());

        primaryStage.setScene(this.home);
        primaryStage.show();
    }

    /**board
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

    /**
     * Getter for the board
     * @return boardCtrl
     */
    public BoardCtrl getBoard() {
        return boardCtrl;
    }

    public void showDetailedTask() {
        primaryStage.setTitle("Task Details");
        primaryStage.setScene(detailedTask);
    }


//
//    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
//            Pair<AddQuoteCtrl, Parent> add) {
//        this.primaryStage = primaryStage;
//        this.overviewCtrl = overview.getKey();
//        this.overview = new Scene(overview.getValue());
//
//        this.addCtrl = add.getKey();
//        this.add = new Scene(add.getValue());
//
//        showOverview();
//        primaryStage.show();
//    }
//
//    public void showOverview() {
//        primaryStage.setTitle("Quotes: Overview");
//        primaryStage.setScene(overview);
//        overviewCtrl.refresh();
//    }
//
//    public void showAdd() {
//        primaryStage.setTitle("Quotes: Adding Quote");
//        primaryStage.setScene(add);
//        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
//    }
}