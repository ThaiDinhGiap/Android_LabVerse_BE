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
}
