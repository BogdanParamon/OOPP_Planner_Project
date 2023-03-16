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
package client.utils;

//import commons.Quote;

import commons.Board;
import commons.Task;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static String SERVER;

    public static void setSERVER(String SERVER) {
        ServerUtils.SERVER = SERVER;
    }

    public boolean validServer() {
        try {
            var x = ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Task addTask(Task task) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(task, APPLICATION_JSON), Task.class);
    }

    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Board>>() {
                });
    }

    public Board addBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }
}