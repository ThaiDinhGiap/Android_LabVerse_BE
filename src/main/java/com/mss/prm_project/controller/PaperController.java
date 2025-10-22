package com.mss.prm_project.controller;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/papers")
public class PaperController {
    private final PaperService paperService;

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
        public ResponseEntity<PaperDTO> uploadPaper(@RequestPart("dto") PaperDTO dto, @RequestParam("publishDate") String publishDate, @RequestParam("file") MultipartFile file) throws IOException {
        if(Objects.isNull(dto.getPriority())) {
            dto.setPriority(1);
        }
        LocalDateTime localDateTime = LocalDateTime.parse(publishDate);
        dto.setPublishDate(localDateTime);
        PaperDTO result = paperService.insertPaper(dto, file);
        return ResponseEntity.ok(result);
    }

    @PostMapping( "/add-to-favourite")
    public ResponseEntity<FavoritePaperDTO> addToFavoritePapers(@RequestParam("userId") long userId , @RequestParam("paperId") long paperId ) {
        FavoritePaperDTO result = paperService.addtoFavoritePapers(userId, paperId);
        return ResponseEntity.ok(result);
    }
}