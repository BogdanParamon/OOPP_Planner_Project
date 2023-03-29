package server.api;


import commons.Subtask;
import commons.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.SubtaskRepository;
import server.database.TaskRepository;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public SubtaskController(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Subtask> add(@RequestParam long taskId, @RequestBody Subtask subtask) {
        if(subtask == null || subtask.subtaskText == null || subtask.subtaskText.isEmpty()
        || !taskRepository.existsById(taskId)) {
            return ResponseEntity.badRequest().build();
        }
        Subtask savedSubtask = subtaskRepository.save(subtask);
        Task task = taskRepository.findById(taskId).get();
        task.addSubtask(savedSubtask);
        taskRepository.save(task);
        return ResponseEntity.ok(savedSubtask);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<Subtask> updateSubtask(@RequestBody Subtask subtask) {
        if(subtask == null || !subtaskRepository.existsById(subtask.subTaskId)) {
            return ResponseEntity.badRequest().build();
        }
        Subtask updatedSubtask =  subtaskRepository.save(subtask);
        return ResponseEntity.ok(updatedSubtask);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Subtask> delete(@RequestParam long subtaskId) {
        if(!subtaskRepository.existsById(subtaskId) || subtaskId < 0) {
            return ResponseEntity.badRequest().build();
        }
        subtaskRepository.deleteById(subtaskId);
        return ResponseEntity.ok().build();
    }
}
