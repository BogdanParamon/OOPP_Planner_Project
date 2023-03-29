package server.api;

import commons.Board;
import commons.Packet;
import commons.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.TaskListRepository;

import java.util.List;

@RestController
@RequestMapping("/api/taskLists")
public class TaskListController {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    /**
     * Constructor for class TaskListController
     *
     * @param taskListRepository      - the repository containing all task lists
     * @param boardRepository - the repository containing all boards
     */
    public TaskListController(TaskListRepository taskListRepository,
                              BoardRepository boardRepository) {
        this.taskListRepository = taskListRepository;
        this.boardRepository = boardRepository;
    }

    /**
     * Gets all task lists from the repository
     *
     * @return a list containing all task lists
     */
    @GetMapping(path = {"", "/"})
    public List<TaskList> getAll() {
        return taskListRepository.findAll();
    }

    /**
     * Gets a task list by id
     *
     * @param id - id of the desired task list
     * @return A response entity object of type TaskList
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") long id) {
        if (id < 0 || !taskListRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(taskListRepository.findById(id).get());
    }

    /**
     * Adds a task list to the repository
     *
     * @param boardId the ID of the board the list will be added to
     * @param list    - list to be added
     * @return a response entity object of type TaskList
     */
    @PostMapping(path = "/add")
    public ResponseEntity<TaskList> add(@RequestBody TaskList list, @RequestParam long boardId) {
        if (list.title == null || list.tasks == null
                || list.title.isEmpty() ||  ! boardRepository.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }
        TaskList saved = taskListRepository.save(list);
        Board parentBoard = boardRepository.findById(boardId).get();
        parentBoard.lists.add(saved);
        boardRepository.save(parentBoard);
        return ResponseEntity.ok(list);
    }

    /**
     * Deletes a task list from the repository
     *
     * @param id - id of the task list to be deleted
     * @return a response entity object of type TaskList
     */
    @DeleteMapping("/delete")
    public ResponseEntity<TaskList> delete(@RequestParam long id) {
        if (id < 0 || !taskListRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        TaskList tl = taskListRepository.findById(id).get();
        taskListRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Updates a task list
     *
     * @param taskList - the new updated list
     * @return a response entity object of type TaskList that confirms to the client that
     * the operation was successful
     */
    @PutMapping("/update")
    public ResponseEntity<TaskList> updateList(@RequestBody TaskList taskList) {
        if (taskList == null || !taskListRepository.existsById(taskList.listId)) {
            return ResponseEntity.badRequest().build();
        }
        TaskList updatedList = taskListRepository.save(taskList);
        return ResponseEntity.ok(updatedList);
    }

    @PutMapping("/rename")
    public ResponseEntity<TaskList> renameList(@RequestParam long listId,
                                               @RequestBody String newTitle) {
        if (newTitle == null || !taskListRepository.existsById(listId)) {
            return ResponseEntity.badRequest().build();
        }
        TaskList taskList = taskListRepository.findById(listId).get();
        taskList.title = newTitle;
        TaskList updatedList = taskListRepository.save(taskList);
        return ResponseEntity.ok(updatedList);
    }

    @MessageMapping("/taskLists/add/{boardId}")
    @SendTo("/topic/taskLists/add/{boardId}")
    @Transactional
    public TaskList addMessage(TaskList taskList, @DestinationVariable("boardId") long boardId) {
        add(taskList, boardId);
        return taskList;
    }

    @MessageMapping("/taskLists/rename/{boardId}")
    @SendTo("/topic/taskLists/rename/{boardId}")
    @Transactional
    public Packet renameMessage(Packet listIdAndNewTitle) {
        renameList(listIdAndNewTitle.longValue, listIdAndNewTitle.stringValue);
        return listIdAndNewTitle;
    }

    @MessageMapping("/taskLists/delete/{boardId}")
    @SendTo("/topic/taskLists/delete/{boardId}")
    @Transactional
    public Long deleteMessage(Long listId) {
        delete(listId);
        return listId;
    }
}
