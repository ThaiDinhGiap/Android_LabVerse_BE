package com.mss.prm_project.repository;

import com.mss.prm_project.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {
    ReadingProgress findByUserUserIdAndPaperPaperId(Long userId, Long paper);
}
