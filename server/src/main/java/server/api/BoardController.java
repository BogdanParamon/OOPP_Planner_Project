package server.api;

import commons.Board;
import commons.Packet;
import commons.Tag;
import commons.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.TagRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;


    /**
     * Constructor for the BoardController class
     *
     * @param boardRepository The repository containing Board instances
     * @param tagRepository  The repository containing Tag instances
     */
    public BoardController(BoardRepository boardRepository, TagRepository tagRepository) {
        this.boardRepository = boardRepository;
        this.tagRepository = tagRepository;
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


    @DeleteMapping(path = "/deleteAll")
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

    @PutMapping("/rename")
    public ResponseEntity<Board> renameBoard(@RequestParam long boardId,
                                               @RequestBody String newTitle) {
        if (newTitle == null || !boardRepository.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }
        Board board = boardRepository.findById(boardId).get();
        board.title = newTitle;
        Board updatedBoard = boardRepository.save(board);
        return ResponseEntity.ok(updatedBoard);
    }

    @PostMapping(path = {"/addTag"})
    public ResponseEntity<Tag> addTag(@RequestParam long boardId, @RequestBody Tag tag) {
        if (!boardRepository.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }
        Tag saved = tagRepository.save(tag);
        Board board = boardRepository.findById(boardId).get();
        board.addTag(saved);
        boardRepository.save(board);
        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = {"/getTagById/{tagId}"})
    public ResponseEntity<Tag> getTag(@PathVariable("tagId") long tagId) {
        if (!tagRepository.existsById(tagId)) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag = tagRepository.findById(tagId).get();
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping(path = {"/deleteTag"})
    public ResponseEntity<Tag> deleteTag(@RequestParam long tagId) {
        if (!tagRepository.existsById(tagId)) {
            return ResponseEntity.badRequest().build();
        }
        tagRepository.deleteById(tagId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = {"/updateTag"})
    public ResponseEntity<Tag> updateTag(@RequestBody Tag tag) {
        if (tag == null || !tagRepository.existsById(tag.tagId)) {
            return ResponseEntity.badRequest().build();
        }
        Tag updatedTag = tagRepository.save(tag);
        return ResponseEntity.ok(updatedTag);
    }

    @MessageMapping("/boards/add")
    @SendTo("/topic/boards/add")
    public Packet addMessage(Board board) {
        add(board);
        Packet titleAndId = new Packet();
        titleAndId.longValue = board.boardId;
        titleAndId.stringValue = board.title;
        return titleAndId;
    }

    @MessageMapping("/boards/update")
    @SendTo("/topic/boards/update")
    public Packet updateMessage(Board board) {
        updateBoard(board);
        Packet packet = new Packet();
        packet.longValue = board.boardId;
        packet.board = board;
        return packet;
    }

    @MessageMapping("/boards/rename/{boardId}")
    @SendTo("/topic/boards/rename/{boardId}")
    @Transactional
    public Packet renameMessage(String newTitle,
                                @DestinationVariable("boardId") long boardId) {
        renameBoard(boardId, newTitle);
        Packet boardIdAndNewTitle = new Packet();
        boardIdAndNewTitle.longValue = boardId;
        boardIdAndNewTitle.stringValue = newTitle;
        return boardIdAndNewTitle;
    }
}
