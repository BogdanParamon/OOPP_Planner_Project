package server.api;


import commons.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
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
        if (subtask == null || subtask.subtaskText == null || subtask.subtaskText.isEmpty()
                || !taskRepository.existsById(taskId)) {
            return ResponseEntity.badRequest().build();
        }
        Subtask savedSubtask = subtaskRepository.save(subtask);
        Task task = taskRepository.findById(taskId).get();
        task.addSubtask(savedSubtask);
        taskRepository.save(task);
        return ResponseEntity.ok(savedSubtask);
    }

//    @PutMapping(path = "/update")
//    public ResponseEntity<Subtask> updateSubtask(@RequestBody Subtask subtask) {
//        if (subtask == null || !subtaskRepository.existsById(subtask.subTaskId)) {
//            return ResponseEntity.badRequest().build();
//        }
//        Subtask updatedSubtask =  subtaskRepository.save(subtask);
//        return ResponseEntity.ok(updatedSubtask);
//    }

    @PutMapping("/rename")
    public ResponseEntity<Subtask> updateSubtaskText(@RequestParam long subtaskID,
                                             @RequestBody String updatedSubtaskText) {
        if (updatedSubtaskText == null || !subtaskRepository.existsById(subtaskID)) {
            return ResponseEntity.badRequest().build();
        }
        Subtask subtask = subtaskRepository.findById(subtaskID).get();
        subtask.subtaskText = updatedSubtaskText;
        Subtask updatedSubtask = subtaskRepository.save(subtask);
        return ResponseEntity.ok(updatedSubtask);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Subtask> delete(@RequestParam long subtaskId, @RequestParam long taskId) {
        if (!subtaskRepository.existsById(subtaskId) || subtaskId < 0
                || !taskRepository.existsById(taskId) || taskId < 0) {
            return ResponseEntity.badRequest().build();
        }
        Task task = taskRepository.findById(taskId).get();
        Subtask subtask = subtaskRepository.findById(subtaskId).get();
        if (task.subtasks.remove(subtask)) {
            subtaskRepository.deleteById(subtaskId);
        }
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/subtasks/add/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/add/{boardId}")
    @Transactional
    public Packet addMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                             @DestinationVariable("listId") long listId,
                             @DestinationVariable("taskId") long taskId) {
        add(taskId, subtask);
        Packet packet = new Packet();
        packet.longValue = taskId;
        packet.longValue2 = listId;
        packet.subtask = subtask;
        return packet;
    }

    @MessageMapping("/subtasks/rename/{subTaskId}")
    @SendTo("/topic/subtasks/rename/{subTaskId}")
    @Transactional
    public Packet renameMessage(String updatedText,
                                @DestinationVariable("subTaskId") long subtaskId) {
        updateSubtaskText(subtaskId, updatedText);
        Packet subtaskIdAndNewText = new Packet();
        subtaskIdAndNewText.stringValue = updatedText;
        subtaskIdAndNewText.longValue = subtaskId;
        return subtaskIdAndNewText;
    }

    @MessageMapping("/subtasks/delete/{taskId}")
    @SendTo("/topic/subtasks/delete/{taskId}")
    @Transactional
    public Packet deleteMessage(Long subtaskId, @DestinationVariable("taskId") long taskId) {
        delete(subtaskId, taskId);
        Packet packet = new Packet();
        packet.longValue = taskId;
        packet.longValue2 = subtaskId;
        return packet;
    }
}
