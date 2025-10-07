package com.mss.prm_project.repository;

import com.mss.prm_project.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
