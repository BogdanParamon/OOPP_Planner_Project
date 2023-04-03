package server.api;

import commons.Board;
import commons.Packet;
import commons.Tag;
import commons.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    /**
     * Gets all the board instances in the repository
     *
     * @return A list containing all boards in the repository
     */
    @GetMapping(path = {"", "/"})
    public List<Board> getAll() {
        return boardService.getAll();
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
        Optional<Board> board = boardService.getByid(id);
        if (board.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(board.get());
    }

    /**
     * Gets the list of TaskLists from the specified board
     *
     * @param boardId the ID of the board which the lists will be retrieved from
     * @return a List conatining the list of TaskLists in the board
     */
    @GetMapping("/{boardId}/taskLists")
    public ResponseEntity<List<TaskList>> getListsByBoardId(@PathVariable("boardId") long boardId) {
        try {
            return ResponseEntity.ok(boardService.getListsByBoardId(boardId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adds a board to the repository
     *
     * @param board  the boards that will get added to the repository
     * @param userId the Id of the user that gets permission to access board
     * @return A ResponseEntity object that can handle all outcomes of
     * attempted addition to the board repository
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Board> add(@RequestBody Board board, @RequestParam long userId) {
        try {
            return ResponseEntity.ok(boardService.add(board, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a board from the database
     *
     * @param boardId id of board to be deleted
     * @return successful if board exists
     */
    @DeleteMapping(path = "/delete")
    @Transactional
    public ResponseEntity<String> delete(@RequestParam long boardId) {
        try {
            return ResponseEntity.ok(boardService.delete(boardId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update a board
     *
     * @param board board to be updated
     * @return updated board
     */
    @PutMapping("/update")
    public ResponseEntity<Board> updateBoard(@RequestBody Board board) {
        try {
            return ResponseEntity.ok(boardService.updateBoard(board));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping(path = "/deleteAll")
    public ResponseEntity<String> deleteAll() {
        return ResponseEntity.ok(boardService.deleteAll());
    }

    @GetMapping(path = "/titles&ids")
    public ResponseEntity<Map<Long, String>> getBoardTitlesAndIds() {
        return ResponseEntity.ok(boardService.getBoardTitlesAndIds());
    }

    @PutMapping("/rename")
    public ResponseEntity<Board> renameBoard(@RequestParam long boardId,
                                             @RequestBody String newTitle) {
        try {
            return ResponseEntity.ok(boardService.renameBoard(boardId, newTitle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = {"/addTag"})
    public ResponseEntity<Tag> addTag(@RequestParam long boardId, @RequestBody Tag tag) {
        try {
            return ResponseEntity.ok(boardService.addTag(boardId, tag));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = {"/getTagById/{tagId}"})
    public ResponseEntity<Tag> getTag(@PathVariable("tagId") long tagId) {
        try {
            return ResponseEntity.ok(boardService.getTag(tagId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(path = {"/deleteTag"})
    public ResponseEntity<String> deleteTag(@RequestParam long tagId) {
        try {
            return ResponseEntity.ok(boardService.deleteTag(tagId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = {"/updateTag"})
    public ResponseEntity<Tag> updateTag(@RequestBody Tag tag) {
        try {
            return ResponseEntity.ok(boardService.updateTag(tag));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/boards/add/{userId}")
    @SendTo("/topic/boards/add/{userId}")
    @Transactional
    public Packet addMessage(Board board, @DestinationVariable("userId") long userId) {
        return boardService.addMessage(board, userId);
    }


    @MessageMapping("/boards/update")
    @SendTo("/topic/boards/update")
    public Packet updateMessage(Board board) {
        return boardService.updateMessage(board);
    }

    @MessageMapping("/boards/rename/{boardId}")
    @SendTo("/topic/boards/rename/{boardId}")
    @Transactional
    public Packet renameMessage(String newTitle,
                                @DestinationVariable("boardId") long boardId) {
        return boardService.renameMessage(newTitle, boardId);
    }
}
