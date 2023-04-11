package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Modifying
    @Query(value = "DELETE FROM TASK_TAGS WHERE TAGS_TAG_ID = ?1", nativeQuery = true)
    void deleteTaskTags(long tagID);
}
