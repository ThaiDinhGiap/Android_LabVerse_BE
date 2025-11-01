package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    @Query("""
        SELECT a FROM Annotation a
        JOIN a.readers r
        WHERE r.userId = :userId
    """)
    List<Annotation> findAllReadableByUserId(@Param("userId") long userId);

    Annotation findByPaperPaperIdAndOwnerUserId(int paperPaperId, int ownerUserId);

    @Query("""
        SELECT DISTINCT a
        FROM Annotation a
        LEFT JOIN a.readers r
        WHERE (a.owner.userId = :userId OR r.userId = :userId)
          AND a.paper.paperId = :paperId
    """)
    List<Annotation> findAllSharableByUserIdAndPaperId(@Param("userId") long userId,
                                                       @Param("paperId") long paperId);

}
