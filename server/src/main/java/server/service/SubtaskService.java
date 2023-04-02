package server.service;

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

    public String delete(long subtaskId) throws IllegalArgumentException {
        if (!subtaskRepository.existsById(subtaskId) || subtaskId < 0) {
            throw new IllegalArgumentException();
        }
        subtaskRepository.deleteById(subtaskId);
        return "Successful";
    }

}
