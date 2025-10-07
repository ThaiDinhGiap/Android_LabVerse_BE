package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
}
