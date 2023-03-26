package server.api;

import commons.Board;
import commons.TaskList;
import org.springframework.http.ResponseEntity;
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
}
