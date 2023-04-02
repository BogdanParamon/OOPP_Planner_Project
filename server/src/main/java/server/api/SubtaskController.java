package server.api;


import commons.Subtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.SubtaskService;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    @PostMapping(path = "/add")
    public ResponseEntity<Subtask> add(@RequestParam long taskId, @RequestBody Subtask subtask) {
        try {
            return ResponseEntity.ok(subtaskService.add(taskId, subtask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/update")
    public ResponseEntity<Subtask> updateSubtask(@RequestBody Subtask subtask) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtask(subtask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam long subtaskId) {
        try {
            return ResponseEntity.ok(subtaskService.delete(subtaskId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
