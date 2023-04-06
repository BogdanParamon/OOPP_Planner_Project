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
package client;

import client.scenes.*;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);


    /**
     * Main method
     *
     * @param args arguments
     * @throws URISyntaxException error
     * @throws IOException        IO exception
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * start the application
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws IOException error
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        var boardOverview =
                FXML.load(BoardOverviewCtrl.class, "client", "scenes", "BoardOverview.fxml");
        var home =
                FXML.load(HomeCtrl.class, "client", "scenes", "Home.fxml");
        var board = FXML.load(BoardCtrl.class, "client", "scenes", "Board.fxml");
        var addTask = FXML.load(AddTaskCtrl.class, "client", "scenes", "AddTask.fxml");
        var userOrAdmin = FXML.load(UserOrAdminCtrl.class, "client", "scenes", "UserOrAdmin.fxml");
        var shortcuts = FXML.load(ShortcutsCtrl.class, "client", "scenes", "Shortcuts.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        primaryStage.initStyle(StageStyle.TRANSPARENT);


        mainCtrl.initialize(primaryStage, home, boardOverview, board, addTask,
                userOrAdmin, shortcuts);
    }
}