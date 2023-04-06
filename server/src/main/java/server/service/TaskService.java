package server.service;

import commons.Packet;
import commons.Tag;
import commons.Task;
import commons.TaskList;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import server.database.TagRepository;
import server.database.TaskListRepository;
import server.database.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    /**
     * The repository object that provides database access for Task objects.
     */
    private final TaskRepository taskRepository;

    private final TaskListRepository taskListRepository;

    private final TagRepository tagRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskListRepository taskListRepository,
                       TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
        this.tagRepository = tagRepository;
    }

    public Task add(long taskListId, Task task) throws IllegalArgumentException {
        if (task == null || task.title == null || task.title.isEmpty()
                || !taskListRepository.existsById(taskListId)) {
            throw new IllegalArgumentException();
        }
        Task saved = taskRepository.save(task);
        TaskList list = taskListRepository.findById(taskListId).get();
        list.tasks.add(0, saved);
        taskListRepository.save(list);
        return saved;
    }

    public Task getTaskById(long id) throws IllegalArgumentException {
        if (id < 0 || !taskRepository.existsById(id)) {
            throw new IllegalArgumentException();
        }
        return taskRepository.findById(id).get();
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasks(String sortBy) {
        List<Task> tasks;

        if (sortBy != null) {
            tasks = taskRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy));
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks;
    }

    public Task updateTask(Task task) throws IllegalArgumentException {
        if (task == null || !taskRepository.existsById(task.taskId)) {
            throw new IllegalArgumentException();
        }
        return taskRepository.save(task);
    }

    public String delete(long taskId, long taskListId) throws IllegalArgumentException {
        if (!taskRepository.existsById(taskId) || taskId < 0
                || !taskListRepository.existsById(taskListId) || taskListId < 0) {
            throw new IllegalArgumentException();
        }
        TaskList taskList = taskListRepository.findById(taskListId).get();
        Task task = taskRepository.findById(taskId).get();
        if (taskList.tasks.remove(task)) {
            taskRepository.deleteTaskTags(taskId);
            taskRepository.deleteById(taskId);
        }
        taskListRepository.save(taskList);
        return "Successful";
    }

    public String dragTask(long taskId, long dragFromListId, long dragToListId,
                           int dragToIndex) throws IllegalArgumentException {
        if (!taskRepository.existsById(taskId) || !taskListRepository.existsById(dragFromListId)
                || !taskListRepository.existsById(dragToListId) || dragToIndex < 0) {
            throw new IllegalArgumentException();
        }
        TaskList taskList1 = taskListRepository.findById(dragFromListId).get();
        TaskList taskList2 = taskListRepository.findById(dragToListId).get();
        Task task = taskRepository.findById(taskId).get();
        if (taskList1.tasks.remove(task)) {
            taskList2.tasks.add(dragToIndex, task);
            taskListRepository.save(taskList1);
            taskListRepository.save(taskList2);
        }
        return "Successful";
    }

    public Task addTag(long tagId, long taskId) throws IllegalArgumentException {
        if (!tagRepository.existsById(tagId) || !taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException();
        }
        Task task = taskRepository.getById(taskId);
        Tag tag = tagRepository.getById(tagId);

        task.addTag(tag);
        taskRepository.save(task);

        return task;
    }

    public Packet addMessage(Task task, long boardId, long listId) {
        add(listId, task);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.task = task;
        return packet;
    }

    public Packet updateMessage(Task task, long boardId, long listId) {
        updateTask(task);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.task = task;
        return packet;
    }

    public Packet deleteMessage(Long taskId, long boardId, long listId) {
        delete(taskId, listId);
        Packet packet = new Packet();
        packet.longValue = listId;
        packet.longValue2 = taskId;
        return packet;
    }

    public Packet dragMessage(Packet packet, long boardId) {
        long taskId = packet.longValue;
        long dragFromListId = packet.longValue2;
        long dragToListId = packet.longValue3;
        int dragToIndex = packet.intValue;
        dragTask(taskId, dragFromListId, dragToListId, dragToIndex);
        return packet;
    }

    public Packet deleteTag(long tagId, long taskId) {
        taskRepository.deleteTagFromTask(taskId, tagId);
        Packet packet = new Packet();
        packet.longValue = tagId;
        return packet;
    }

}
