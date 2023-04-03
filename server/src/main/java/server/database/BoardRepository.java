package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Modifying
    @Query(value = "DELETE FROM REGISTRATION WHERE BOARD_ID = ?1", nativeQuery = true)
    void deleteBoardUserConnection(long boardId);
}
