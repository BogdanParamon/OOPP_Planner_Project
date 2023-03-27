package server.api;

import commons.Board;
import commons.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardRepository boardRepository;

    /**
     * Constructor for the BoardController class
     *
     * @param boardRepository The repository containing Board instances
     */
    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Gets all the board instances in the repository
     *
     * @return A list containing all boards in the repository
     */
    @GetMapping(path = {"", "/"})
    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    /**
     * Gets a Board by its specified ID
     *
     * @param id the ID of the board intended to be retrieved
     * @return ResponseEntity object that can handle all outcomes
     * of the attempted retrieval of board by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        if (id < 0 || !boardRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(boardRepository.findById(id).get());
    }

    /**
     * Gets the list of TaskLists from the specified board
     *
     * @param boardId the ID of the board which the lists will be retrieved from
     * @return a List conatining the list of TaskLists in the board
     */
    @GetMapping("/{boardId}/taskLists")
    public ResponseEntity<List<TaskList>> getListsByBoardId(@PathVariable("boardId") long boardId) {
        if (boardId < 0 || !boardRepository.existsById(boardId))
            return ResponseEntity.badRequest().build();
        Board parentBoard = boardRepository.findById(boardId).get();
        return ResponseEntity.ok(parentBoard.lists);
    }

    /**
     * Adds a board to the repository
     *
     * @param board the boards that will get added to the repository
     * @return A ResponseEntity object that can handle all outcomes of
     * attempted addition to the board repository
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        if (board == null || board.title == null || board.title.isEmpty())
            return ResponseEntity.badRequest().build();

        Board saved = boardRepository.save(board);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes a board from the database
     *
     * @param boardId id of board to be deleted
     * @return successful if board exists
     */
    @DeleteMapping(path = "/delete")
    public ResponseEntity<Board> delete(@RequestParam long boardId) {
        if (!boardRepository.existsById(boardId))
            return ResponseEntity.badRequest().build();

        boardRepository.deleteById(boardId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update a board
     *
     * @param board board to be updated
     * @return updated board
     */
    @PutMapping("/update")
    public ResponseEntity<Board> updateBoard(@RequestBody Board board) {
        if (board == null || !boardRepository.existsById(board.boardId)) {
            return ResponseEntity.badRequest().build();
        }
        Board updatedBoard = boardRepository.save(board);
        return ResponseEntity.ok(updatedBoard);
    }


    @PostMapping(path = "/deleteAll")
    public ResponseEntity<String> deleteAll() {
        boardRepository.deleteAll();
        return ResponseEntity.ok("Successful");
    }

    @GetMapping(path = "/titles&ids")
    public ResponseEntity<Map<Long, String>> getBoardTitlesAndIds() {
        Map<Long, String> map = new HashMap<>();
        for (Board board : boardRepository.findAll())
            map.put(board.boardId, board.title);
        return ResponseEntity.ok(map);
    }

    @MessageMapping("/boards")
    @SendTo("/topic/boards")
    public List<Object> addMessage(Board board) {
        add(board);
        List<Object> titleAndId = new ArrayList<>(2);
        titleAndId.add(board.boardId);
        titleAndId.add(board.title);
        return titleAndId;
    }
}
