package com.mss.prm_project.repository;

import com.mss.prm_project.entity.SearchQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchQueryRepository extends JpaRepository<SearchQuery, Long> {
}
