package com.mss.prm_project.service;

import com.mss.prm_project.dto.ReadingProgressDTO;

public interface ReadingProgressService {
    ReadingProgressDTO createReadingProgressForUserAndPaper(Long userId, Long paperId, int lastReadPage, int totalPages);
}
