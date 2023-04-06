package server.api;


import commons.*;
import commons.Subtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
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

    @PutMapping(path = "/status")
    public ResponseEntity<Subtask> updateStatus(@RequestBody Subtask subtask) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtask(subtask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/rename")
    public ResponseEntity<Subtask> rename(@RequestParam Subtask subtask) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtask(subtask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam long subtaskId, @RequestParam long taskId) {
        try {
            return ResponseEntity.ok(subtaskService.delete(subtaskId, taskId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/subtasks/add/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/add/{boardId}")
    @Transactional
    public Packet addMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                             @DestinationVariable("listId") long listId,
                             @DestinationVariable("taskId") long taskId) {
        return subtaskService.addMessage(subtask, boardId, listId, taskId);
    }

    @MessageMapping("/subtasks/rename/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/rename/{boardId}")
    @Transactional
    public Packet renameMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        return subtaskService.renameMessage(subtask, boardId, listId, taskId);
    }

    @MessageMapping("/subtasks/delete/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/delete/{boardId}")
    @Transactional
    public Packet deleteMessage(Long subtaskId, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        return subtaskService.deleteMessage(subtaskId,boardId,listId, taskId);
    }

    @MessageMapping("/subtasks/status/{boardId}/{listId}/{taskId}")
    @SendTo("/topic/subtasks/status/{boardId}")
    @Transactional
    public Packet statusMessage(Subtask subtask, @DestinationVariable("boardId") long boardId,
                                @DestinationVariable("listId") long listId,
                                @DestinationVariable("taskId") long taskId) {
        return subtaskService.statusMessage(subtask, boardId, listId, taskId);
    }
}
