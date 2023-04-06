package server.service;

import commons.Packet;
import commons.Subtask;
import commons.Task;
import org.springframework.stereotype.Service;
import server.database.SubtaskRepository;
import server.database.TaskRepository;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    public Subtask add(long taskId, Subtask subtask) throws IllegalArgumentException {
        if (subtask == null || subtask.subtaskText == null || subtask.subtaskText.isEmpty()
                || !taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException();
        }
        Subtask savedSubtask = subtaskRepository.save(subtask);
        Task task = taskRepository.findById(taskId).get();
        task.addSubtask(savedSubtask);
        taskRepository.save(task);
        return savedSubtask;
    }

    public Subtask updateSubtask(Subtask subtask) throws IllegalArgumentException {
        if (subtask == null || !subtaskRepository.existsById(subtask.subTaskId)) {
            throw new IllegalArgumentException();
        }
        return subtaskRepository.save(subtask);
    }

    public String delete(long subtaskId, long taskId) throws IllegalArgumentException {
        if (!subtaskRepository.existsById(subtaskId) || subtaskId < 0
                || !taskRepository.existsById(taskId) || taskId < 0) {
            throw new IllegalArgumentException();
        }
        Task task = taskRepository.findById(taskId).get();
        Subtask subtask = subtaskRepository.findById(subtaskId).get();
        if (task.subtasks.remove(subtask)) {
            subtaskRepository.deleteById(subtaskId);
        }
        taskRepository.save(task);
        return "Successful";
    }

    public Packet addMessage(Subtask subtask, long boardId, long listId, long taskId) {
        add(taskId, subtask);
        Packet packet = new Packet();
        packet.longValue = taskId;
        packet.longValue2 = listId;
        packet.subtask = subtask;
        return packet;
    }

    public Packet renameMessage(Subtask subtask, long boardId, long listId, long taskId) {
        updateSubtask(subtask);
        Packet packet = new Packet();
        packet.longValue = taskId;
        packet.longValue2 = listId;
        packet.subtask = subtask;
        return packet;
    }

    public Packet deleteMessage(Long subtaskId, long boardId, long listId, long taskId) {
        delete(subtaskId, taskId);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.longValue2 = taskId;
        packet.longValue3 = subtaskId;
        return packet;
    }

    public Packet statusMessage(Subtask subtask, long boardId, long listId, long taskId) {
        updateSubtask(subtask);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.longValue2 = taskId;
        packet.subtask = subtask;
        return packet;
    }

}
