package com.mss.prm_project.controller;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.service.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {
    private final ReadingProgressService readingProgressService;

    @PostMapping("/create")
    public ResponseEntity<ReadingProgressDTO> createReadingProgress(
            @RequestParam Long userId,
            @RequestParam Long paperId,
            @RequestParam int lastReadPage,
            @RequestParam int totalPages) {

        ReadingProgressDTO readingProgressDTO = readingProgressService.createReadingProgressForUserAndPaper(userId, paperId, lastReadPage, totalPages);

        return new ResponseEntity<>(readingProgressDTO, HttpStatus.CREATED);
    }

}
