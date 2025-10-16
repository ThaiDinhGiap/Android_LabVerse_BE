package com.mss.prm_project.controller;

import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}