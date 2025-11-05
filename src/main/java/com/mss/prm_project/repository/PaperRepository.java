package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    @Query("select p from Paper p where p not in (select rp.paper from ReadingProgress rp where rp.user.userId = :userId)")
    List<Paper> findUnreadByUser(@Param("userId") int userId);
    List<Paper> findByUserUserId(Integer userId);

    @Query("SELECT p FROM Paper p " +
            "WHERE " +
            "(:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(p.author) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(p.journal) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(p.publisher) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:author IS NULL OR LOWER(p.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:journal IS NULL OR LOWER(p.journal) LIKE LOWER(CONCAT('%', :journal, '%'))) " +
            "AND (:priority IS NULL OR p.priority = :priority) " +
            "AND (:publisher IS NULL OR LOWER(p.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))) " +
            "AND (:date IS NULL OR FUNCTION('DATE', p.publishDate) = :date) " +
            "AND (:fromDate IS NULL OR p.publishDate >= :fromDate) " +
            "AND (:toDate IS NULL OR p.publishDate <= :toDate) " +
            "AND (:userId IS NULL OR p.user.userId = :userId) "+
            "ORDER BY p.publishDate DESC")
    List<Paper> searchPapers(
            @Param("q") String q,
            @Param("author") String author,
            @Param("journal") String journal,
            @Param("priority") Integer priority,
            @Param("publisher") String publisher,
            @Param("date") LocalDate date,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Integer userId
    );

    @Query("SELECT p FROM Paper p WHERE p.updatedAt > :lastSync")
    List<Paper> findAllUpdatedAfter(@Param("lastSync") LocalDateTime lastSync);

    Paper findByPaperId(int paperId);

    List<Paper> findByPublishDateAfter(LocalDateTime lastSync);
}
