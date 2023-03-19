package server.api;

import commons.Task;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TaskRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    /**
     * The repository object that provides database access for Task objects.
     */
    private final TaskRepository taskRepository;

    /**
     * Constructor for TaskController.
     *
     * @param taskRepository The TaskRepository object to be used for database access.
     */

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Endpoint for adding a new task.
     *
     * @param task The Task object to be added to the database.
     * @return ResponseEntity with the status code whether it's success or failure.
     */

    @PostMapping(path = {"", "/"})
    public ResponseEntity<Task> add(@RequestBody Task task) {
        if (task == null || task.title == null || task.title.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        System.out.println(task);
        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint for getting a task by its ID.
     * @param id The ID of the Task to be retrieved.
     * @return ResponseEntity with the Task object if found or a not found status code.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint for getting all tasks.
     * @return ResponseEntity with a list of all Task objects.
     */

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Task>> getAll() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    /**
     * Endpoint for getting all tasks sorted by the specified attribute.
     * @param sortBy The attribute to sort the tasks by (optional).
     * @return ResponseEntity with a list of Task objects sorted by the specified attribute.
     */

    @GetMapping(value = "/sorted")
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        List<Task> tasks;

        if (sortBy != null) {
            tasks = taskRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy));
        } else {
            tasks = taskRepository.findAll();
        }

        return ResponseEntity.ok(tasks);
    }

    /**
     * Endpoint for updating a task by its ID.
     * @param id The ID of the Task to be updated.
     * @param task The Task object containing the updated information.
     * @return ResponseEntity with the updated Task object or a not found status code.
     */

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    task.setId(id);
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint for deleting a task by ID.
     *
     * @param id The ID of the Task to be deleted.
     * @return ResponseEntity with the status code whether it's success or failure.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> delete(@PathVariable("id") long id) {
        if (id < 0 || !taskRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Task task = taskRepository.getById(id);

        taskRepository.deleteById(id);

        return ResponseEntity.ok(task);
    }
}

