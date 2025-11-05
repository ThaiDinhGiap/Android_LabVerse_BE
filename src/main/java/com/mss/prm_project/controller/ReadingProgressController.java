package com.mss.prm_project.controller;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.service.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {
    private final ReadingProgressService readingProgressService;

    @PostMapping("/create")
    public ResponseEntity<ReadingProgressDTO> createReadingProgress(
            @RequestParam(value = "collectionId", required = false) Integer collectionId,
            @RequestParam("paperId") int paperId,
            @RequestParam("lastReadPage") int lastReadPage,
            @RequestParam("totalPages") int totalPages) {
        if (collectionId == null) {
            collectionId = -1;
        }

        ReadingProgressDTO readingProgressDTO = readingProgressService.createReadingProgressForUserAndPaper(collectionId, paperId, lastReadPage, totalPages);

        return new ResponseEntity<>(readingProgressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/collection")
    public ResponseEntity<List<ReadingProgressDTO>> getAllReadingProgressByCollection(@RequestParam("collectionId") int collectionId, @RequestParam(value = "paperId", defaultValue = "0") int paperId) {
        List<ReadingProgressDTO> readingProgressDTOs = readingProgressService.getAllReadingProgressByCollection(collectionId, paperId);
        return new ResponseEntity<>(readingProgressDTOs, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ReadingProgressDTO>> getPersonalReadingProgress() {
        List<ReadingProgressDTO> results =  readingProgressService.getPersonalReadingProgress();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
