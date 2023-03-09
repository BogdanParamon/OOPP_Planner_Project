package server.api;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import commons.Task;
import server.database.TaskRepository;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Task> add(@RequestBody Task task) {
        if (task == null || task.title == null || task.list == null || task.title.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping(path = { "", "/" })
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

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

