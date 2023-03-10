package server.api;

import commons.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TaskListRepository;

import java.util.List;

@RestController
@RequestMapping("/api/taskList")
public class TaskListController {
    private final TaskListRepository repository;

    /**
     * Constructor for class TaskListController
     *
     * @param repository - the repository containing all task lists
     */
    public TaskListController(TaskListRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets all task lists from the repository
     *
     * @return a list containing all task lists
     */
    @GetMapping(path = { "", "/" })
    public List<TaskList> getAll() {
        return repository.findAll();
    }

    /**
     * Gets a task list by id
     *
     * @param id - id of the desired task list
     * @return A response entity object of type TaskList
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repository.findById(id).get());
    }

    /**
     * Adds a task list to the repository
     *
     * @param list - list to be added
     * @return a response entity object of type TaskList
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<TaskList> add(@RequestBody TaskList list) {
        if (list.title == null || list.board == null || list.tasks == null) {
            return ResponseEntity.badRequest().build();
        }
        TaskList saved = repository.save(list);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes a task list from the repository
     *
     * @param id - id of the task list to be deleted
     * @return a response entity object of type TaskList
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskList> delete(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        TaskList tl = repository.getById(id);
        repository.deleteById(id);

        return ResponseEntity.ok(tl);
    }
}