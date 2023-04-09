package server.service;

import commons.*;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.TagRepository;
import server.database.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository,
                        TagRepository tagRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    public Optional<Board> getByid(long id) {
        return boardRepository.findById(id);
    }

    public List<TaskList> getListsByBoardId(long boardId) throws IllegalArgumentException {
        if (boardId < 0 || !boardRepository.existsById(boardId))
            throw new IllegalArgumentException();
        Board parentBoard = boardRepository.findById(boardId).get();
        return parentBoard.lists;
    }

    public Board add(Board board, long userId) throws IllegalArgumentException {
        if (board == null || board.title == null || board.title.isEmpty())
            throw new IllegalArgumentException();
        User user = userRepository.findById(userId).get();
        Board saved = boardRepository.save(board);
        user.boards.add(saved);
        return saved;
    }

    public Board join(Board board, long userId) {
        User user = userRepository.findById(userId).get();
        user.boards.add(board);
        userRepository.save(user);
        return board;
    }

    public String delete(long boardId) throws IllegalArgumentException {
        if (!boardRepository.existsById(boardId))
            throw new IllegalArgumentException();

        boardRepository.deleteBoardUserConnection(boardId);
        boardRepository.deleteById(boardId);
        return "Successful";
    }

    public Board updateBoard(Board board) throws IllegalArgumentException {
        if (board == null || !boardRepository.existsById(board.boardId)) {
            throw new IllegalArgumentException();
        }
        Board updatedBoard = boardRepository.save(board);
        return updatedBoard;
    }

    public String deleteAll() {
        boardRepository.deleteAll();
        return "Successful";
    }

    public Map<Long, String> getBoardTitlesAndIds() {
        Map<Long, String> map = new HashMap<>();
        for (Board board : boardRepository.findAll())
            map.put(board.boardId, board.title);
        return map;
    }

    public Board renameBoard(long boardId, String newTitle) throws IllegalArgumentException {
        if (newTitle == null || !boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException();
        }
        Board board = boardRepository.findById(boardId).get();
        board.title = newTitle;
        Board updatedBoard = boardRepository.save(board);
        return updatedBoard;
    }

    public Board changePassword(long boardId, String newPassword) throws IllegalArgumentException {
        if (newPassword == null || !boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException();
        }
        Board board = boardRepository.findById(boardId).get();
        board.setPassword(newPassword);
        Board updatedBoard = boardRepository.save(board);
        return updatedBoard;
    }

    public Tag addTag(long boardId, Tag tag) throws IllegalArgumentException {
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException();
        }
        Tag saved = tagRepository.save(tag);
        Board board = boardRepository.findById(boardId).get();
        board.addTag(saved);
        boardRepository.save(board);
        return saved;
    }

    public Tag getTag(long tagId) throws IllegalArgumentException {
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException();
        }
        Tag tag = tagRepository.findById(tagId).get();
        return tag;
    }

    public String deleteTag(long tagId) throws IllegalArgumentException {
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException();
        }
        tagRepository.deleteTaskTags(tagId);
        tagRepository.deleteById(tagId);
        return "Successful";
    }

    public Tag updateTag(Tag tag) throws IllegalArgumentException {
        if (tag == null || !tagRepository.existsById(tag.tagId)) {
            throw new IllegalArgumentException();
        }
        Tag updatedTag = tagRepository.save(tag);
        return updatedTag;
    }

    public Packet addMessage(Board board, long userId) {
        add(board, userId);
        Packet packet = new Packet();
        packet.stringValue = board.title;
        packet.longValue = board.boardId;
        packet.longValue2 = userId;
        return packet;
    }

    public Packet updateMessage(Board board) {
        updateBoard(board);
        Packet packet = new Packet();
        packet.longValue = board.boardId;
        packet.board = board;
        return packet;
    }

    public Packet renameMessage(String newTitle, long boardId) {
        renameBoard(boardId, newTitle);
        Packet boardIdAndNewTitle = new Packet();
        boardIdAndNewTitle.longValue = boardId;
        boardIdAndNewTitle.stringValue = newTitle;
        return boardIdAndNewTitle;
    }

    public Packet changePasswordMessage(String newPassword, long boardId) {
        changePassword(boardId, newPassword);
        Packet boardIdAndNewPassword = new Packet();
        boardIdAndNewPassword.longValue = boardId;
        boardIdAndNewPassword.stringValue = newPassword;
        return boardIdAndNewPassword;
    }

    public Packet joinMessage(Board board, long userId) {
        join(board, userId);
        Packet packet = new Packet();
        packet.stringValue = board.title;
        packet.longValue = board.boardId;
        packet.longValue2 = userId;
        return packet;
    }
}
