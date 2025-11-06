package com.mss.prm_project.repository;

import com.mss.prm_project.entity.ReadingList;
import com.mss.prm_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {
    // 1. Lấy tất cả danh sách do một người dùng sở hữu (dùng cho /me)
    List<ReadingList> findByOwnerUserUserId(int userId);
    // 2. Tải danh sách chi tiết, bao gồm cả các bài báo bên trong
    @Query("SELECT r FROM ReadingList r LEFT JOIN FETCH r.papers WHERE r.readingId = :listId")
    Optional<ReadingList> findByIdWithPapers(int listId);

    // 3. Kiểm tra quyền sở hữu
    Optional<ReadingList> findByReadingIdAndOwnerUserUserId(int listId, int ownerId);

    List<ReadingList> findDistinctByOwnerUserUserIdOrViewersUserId(int ownerId, int viewerId);


    @Query(value = """
        SELECT COUNT(*) 
        FROM reading_list_paper 
        WHERE reading_id = :listId AND paper_id = :paperId
        """, nativeQuery = true)
    int countPaperInList(@Param("listId") int listId, @Param("paperId") int paperId);


}
