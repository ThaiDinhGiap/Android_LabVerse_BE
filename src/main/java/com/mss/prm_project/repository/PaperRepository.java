package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    @Query("select p from Paper p where p not in (select rp.paper from ReadingProgress rp where rp.user.userId = :userId)")
    Page<Paper> findUnreadByUser(@Param("userId") int userId, Pageable pageable);
    List<Paper> findByUserUserId(Integer userId);
}
