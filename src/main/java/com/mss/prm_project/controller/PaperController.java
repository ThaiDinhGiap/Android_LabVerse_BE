package com.mss.prm_project.controller;

import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;

    @Autowired
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/newest-unread")
    public ResponseEntity<List<Paper>> getNewestUnread(@RequestParam int userId) {
        List<Paper> papers = paperService.getTop10NewestUnreadPapers(userId);
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/list-papers-for-PIs")
    public ResponseEntity<List<?>> getPapersByUserIdWithPIRole(@RequestParam int userId){
        List<PaperDTO> paperDTOs = paperService.getPaperByUserId(userId);
        return ResponseEntity.ok(paperDTOs);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PaperDTO> uploadPaper(@RequestPart("dto") PaperDTO dto,  @RequestParam("file") MultipartFile file) throws IOException {
        PaperDTO result = paperService.insertPaper(dto, file);
        return ResponseEntity.ok(result);
    }
}