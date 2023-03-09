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

    public TaskListController(TaskListRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = { "", "/" })
    public List<TaskList> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repository.findById(id).get());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<TaskList> add(@RequestBody TaskList list) {
        if (list.title == null || list.board == null || list.tasks == null) {
            return ResponseEntity.badRequest().build();
        }
        TaskList saved = repository.save(list);
        return ResponseEntity.ok(saved);
    }

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
