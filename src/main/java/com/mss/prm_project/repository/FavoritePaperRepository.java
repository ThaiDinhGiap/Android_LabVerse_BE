package com.mss.prm_project.repository;

import com.mss.prm_project.entity.FavoritePaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface FavoritePaperRepository extends JpaRepository<FavoritePaper, Long> {
    FavoritePaper findByUserUserIdAndPaperPaperId(@RequestParam Long userId, @RequestParam Long paperId);
    List<FavoritePaper> findAllByUserUserId(@RequestParam int userId);
}
