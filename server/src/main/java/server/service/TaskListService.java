package server.service;

import commons.Board;
import commons.Packet;
import commons.TaskList;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.TaskListRepository;

import java.util.List;

@Service
public class TaskListService {

    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    public TaskListService(TaskListRepository taskListRepository, BoardRepository boardRepository) {
        this.taskListRepository = taskListRepository;
        this.boardRepository = boardRepository;
    }

    public List<TaskList> getAll() {
        return taskListRepository.findAll();
    }

    public TaskList getById(long id) throws IllegalArgumentException {
        if (id < 0 || !taskListRepository.existsById(id)) {
            throw new IllegalArgumentException();
        }
        return taskListRepository.findById(id).get();
    }

    public TaskList add(TaskList list, long boardId) throws IllegalArgumentException {
        if (list.title == null || list.tasks == null
                || list.title.isEmpty() || !boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException();
        }
        TaskList saved = taskListRepository.save(list);
        Board parentBoard = boardRepository.findById(boardId).get();
        parentBoard.lists.add(saved);
        boardRepository.save(parentBoard);
        return list;
    }

    public String delete(long id) throws IllegalArgumentException {
        if (id < 0 || !taskListRepository.existsById(id)) {
            throw new IllegalArgumentException();
        }
        TaskList tl = taskListRepository.findById(id).get();
        taskListRepository.deleteById(id);

        return "Successful";
    }

    public TaskList updateList(TaskList taskList) throws IllegalArgumentException {
        if (taskList == null || !taskListRepository.existsById(taskList.listId)) {
            throw new IllegalArgumentException();
        }
        TaskList updatedList = taskListRepository.save(taskList);
        return updatedList;
    }

    public TaskList renameList(long listId, String newTitle) throws IllegalArgumentException {
        if (newTitle == null || !taskListRepository.existsById(listId)) {
            throw new IllegalArgumentException();
        }
        TaskList taskList = taskListRepository.findById(listId).get();
        taskList.title = newTitle;
        TaskList updatedList = taskListRepository.save(taskList);
        return updatedList;
    }

    public TaskList addMessage(TaskList taskList, long boardId) {
        add(taskList, boardId);
        return taskList;
    }

    public Packet renameMessage(Packet listIdAndNewTitle) {
        renameList(listIdAndNewTitle.longValue, listIdAndNewTitle.stringValue);
        return listIdAndNewTitle;
    }

    public Long deleteMessage(Long listId) {
        delete(listId);
        return listId;
    }

}
