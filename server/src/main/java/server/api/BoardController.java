package server.api;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardRepository repo;

    /**
     * Constructor for the BoardController class
     *
     * @param repo The repository containing Board instances
     */
    public BoardController(BoardRepository repo) {
        this.repo = repo;
    }

    /**
     * Gets all the board instances in the repository
     *
     * @return A list containing all boards in the repository
     */
    @GetMapping(path = { "", "/" })
    public List<Board> getAll() {
        return repo.findAll();
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
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a board to the repository
     *
     * @param board the boards that will get added to the repository
     * @return A ResponseEntity object that can handle all outcomes of
     * attempted addition to the board repository
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Board> add(@RequestBody Board board) {
        if (board == null || board.title == null || board.title.isEmpty())
            return ResponseEntity.badRequest().build();

        Board saved = repo.save(board);
        return ResponseEntity.ok(saved);
    }
}
