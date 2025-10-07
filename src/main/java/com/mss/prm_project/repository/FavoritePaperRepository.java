package com.mss.prm_project.repository;

import com.mss.prm_project.entity.FavoritePaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePaperRepository extends JpaRepository<FavoritePaper, Long> {
}
