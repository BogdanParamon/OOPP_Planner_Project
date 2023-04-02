package server.api;

import commons.Packet;
import commons.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import server.service.TaskService;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    /**
     * Endpoint for adding a new task.
     * @param taskListId taskList to be added to
     * @param task The Task object to be added to the database.
     * @return ResponseEntity with the status code whether it's success or failure.
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Task> add(@RequestParam long taskListId, @RequestBody Task task) {
        try {
            Task saved = taskService.add(taskListId, task);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Endpoint for getting a task by its ID.
     * @param id The ID of the Task to be retrieved.
     * @return ResponseEntity with the Task object if found or a not found status code.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Endpoint for getting all tasks.
     * @return ResponseEntity with a list of all Task objects.
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Task>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }

    /**
     * Endpoint for getting all tasks sorted by the specified attribute.
     * @param sortBy The attribute to sort the tasks by (optional).
     * @return ResponseEntity with a list of Task objects sorted by the specified attribute.
     */
    @GetMapping(value = "/sorted")
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        return ResponseEntity.ok(taskService.getAllTasks(sortBy));
    }

    /**
     * Endpoint for updating a task by its ID.
     * @param task The Task object containing the updated information.
     * @return ResponseEntity with the updated Task object or a not found status code.
     */

    @PutMapping(path = "/update")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(task));
    }

    /**
     * Endpoint for deleting a task by ID.
     *
     * @param taskId The ID of the Task to be deleted.
     * @param taskListId The ID of the TaskList in which the task is.
     * @return ResponseEntity with the status code whether it's success or failure.
     */

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam long taskId, @RequestParam long taskListId) {
        try {
            return ResponseEntity.ok(taskService.delete(taskId, taskListId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/drag")
    public ResponseEntity<String> dragTask(@RequestParam long taskId,
                                         @RequestParam long dragFromListId,
                                         @RequestParam long dragToListId,
                                         @RequestParam int dragToIndex) {
        try {
            return ResponseEntity.ok(taskService.dragTask(taskId,
                    dragFromListId, dragToListId, dragToIndex));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping(path = {"/addTag"})
    public ResponseEntity<Task> addTag(@RequestParam long tagId, @RequestParam long taskId) {
        try {
            return ResponseEntity.ok(taskService.addTag(tagId, taskId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/tasks/add/{boardId}/{listId}")
    @SendTo("/topic/tasks/add/{boardId}")
    @Transactional
    public Packet addMessage(Task task, @DestinationVariable("boardId") long boardId,
                             @DestinationVariable("listId") long listId) {
        return taskService.addMessage(task, boardId, listId);
    }

    @MessageMapping("/tasks/update/{boardId}/{listId}")
    @SendTo("/topic/tasks/update/{boardId}")
    @Transactional
    public Packet updateMessage(Task task, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId) {
        return taskService.updateMessage(task, boardId, listId);
    }

    @MessageMapping("/tasks/delete/{boardId}/{listId}")
    @SendTo("/topic/tasks/delete/{boardId}")
    @Transactional
    public Packet deleteMessage(Long taskId, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId) {
        return taskService.deleteMessage(taskId, boardId, listId);
    }

    @MessageMapping("/tasks/drag/{boardId}")
    @SendTo("/topic/tasks/drag/{boardId}")
    @Transactional
    public Packet dragMessage(Packet packet, @DestinationVariable("boardId") long boardId) {
        return taskService.dragMessage(packet, boardId);
    }
}

