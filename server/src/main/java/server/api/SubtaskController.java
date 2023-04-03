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

    @PutMapping(path = "/status")
    public ResponseEntity<Subtask> updateStatus(@RequestBody Subtask subtask) {
        if (subtask == null || !subtaskRepository.existsById(subtask.subTaskId)) {
            return ResponseEntity.badRequest().build();
        }
        Subtask updatedSubtask =  subtaskRepository.save(subtask);
        return ResponseEntity.ok(updatedSubtask);
    }

    @PutMapping("/rename")
    public ResponseEntity<Subtask> rename(@RequestParam Subtask subtask) {
        if (subtask == null || !subtaskRepository.existsById(subtask.subTaskId)) {
            return ResponseEntity.badRequest().build();
        }
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

    @MessageMapping("/subtasks/rename/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/rename/{boardId}")
    @Transactional
    public Packet renameMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        rename(subtask);
        Packet packet = new Packet();
        packet.longValue = taskId;
        packet.longValue2 = listId;
        packet.subtask = subtask;
        return packet;
    }

    @MessageMapping("/subtasks/delete/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/delete/{boardId}")
    @Transactional
    public Packet deleteMessage(Long subtaskId, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        delete(subtaskId, taskId);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.longValue2 = taskId;
        packet.longValue3 = subtaskId;
        return packet;
    }

    @MessageMapping("/subtasks/status/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/status/{boardId}")
    @Transactional
    public Packet statusMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        updateStatus(subtask);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.longValue2 = taskId;
        packet.subtask = subtask;
        return packet;
    }
}
