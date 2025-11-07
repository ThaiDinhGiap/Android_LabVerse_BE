package com.mss.prm_project.service;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.entity.ReadingProgress;

import java.util.List;

public interface ReadingProgressService {
    ReadingProgressDTO createReadingProgressForUserAndPaper(int collectionId, int paperId, int lastReadPage, int totalPages);
    List<ReadingProgressDTO> getAllReadingProgressByCollection(int collectionId);
    List<ReadingProgressDTO> getPersonalReadingProgress();
}
