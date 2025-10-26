package com.mss.prm_project.repository;

import com.mss.prm_project.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByPaperPaperId(long id);
}
