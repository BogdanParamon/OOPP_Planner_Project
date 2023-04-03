package server.api;

import commons.Packet;
import commons.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import server.service.TaskListService;

import java.util.List;

@RestController
@RequestMapping("/api/taskLists")
public class TaskListController {

    @Autowired
    private TaskListService taskListService;

    /**
     * Gets all task lists from the repository
     *
     * @return a list containing all task lists
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<TaskList>> getAll() {
        return ResponseEntity.ok(taskListService.getAll());
    }

    /**
     * Gets a task list by id
     *
     * @param id - id of the desired task list
     * @return A response entity object of type TaskList
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok(taskListService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adds a task list to the repository
     *
     * @param boardId the ID of the board the list will be added to
     * @param list    - list to be added
     * @return a response entity object of type TaskList
     */
    @PostMapping(path = "/add")
    public ResponseEntity<TaskList> add(@RequestBody TaskList list, @RequestParam long boardId) {
        try {
            return ResponseEntity.ok(taskListService.add(list, boardId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a task list from the repository
     *
     * @param id - id of the task list to be deleted
     * @return a response entity object of type TaskList
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam long id) {
        try {
            return ResponseEntity.ok(taskListService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates a task list
     *
     * @param taskList - the new updated list
     * @return a response entity object of type TaskList that confirms to the client that
     * the operation was successful
     */
    @PutMapping("/update")
    public ResponseEntity<TaskList> updateList(@RequestBody TaskList taskList) {
        try {
            return ResponseEntity.ok(taskListService.updateList(taskList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/rename")
    public ResponseEntity<TaskList> renameList(@RequestParam long listId,
                                               @RequestBody String newTitle) {
        try {
            return ResponseEntity.ok(taskListService.renameList(listId, newTitle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/taskLists/add/{boardId}")
    @SendTo("/topic/taskLists/add/{boardId}")
    @Transactional
    public TaskList addMessage(TaskList taskList, @DestinationVariable("boardId") long boardId) {
        return taskListService.addMessage(taskList, boardId);
    }

    @MessageMapping("/taskLists/rename/{boardId}")
    @SendTo("/topic/taskLists/rename/{boardId}")
    @Transactional
    public Packet renameMessage(Packet listIdAndNewTitle) {
        return taskListService.renameMessage(listIdAndNewTitle);
    }

    @MessageMapping("/taskLists/delete/{boardId}")
    @SendTo("/topic/taskLists/delete/{boardId}")
    @Transactional
    public Long deleteMessage(Long listId) {
        return taskListService.deleteMessage(listId);
    }
}
