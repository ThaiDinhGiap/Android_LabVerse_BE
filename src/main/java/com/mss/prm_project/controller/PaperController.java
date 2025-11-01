package com.mss.prm_project.controller;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.service.PaperService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/papers")
public class PaperController {
    private final PaperService paperService;

    @GetMapping("/{id}")
    public ResponseEntity<PaperDTO> getPaperDetail(@PathVariable("id") int id) {
        PaperDTO papers = paperService.findByPaperId(id);
        return ResponseEntity.ok(papers);
    }


    @GetMapping("/list-papers-for-PIs")
    public ResponseEntity<List<PaperDTO>> getPapersByUserIdWithPIRole(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String journal,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer userId){ {
        List<PaperDTO> results = paperService.getPaperByUserId(q, author, journal, priority, publisher, date, fromDate, toDate, userId);
        return ResponseEntity.ok(results);
    }
    }


    @GetMapping("/newest-unread")
    public ResponseEntity<List<PaperDTO>> getNewestUnread(@RequestParam int userId) {
        List<PaperDTO> papers = paperService.getTop10NewestUnreadPapers(userId);
        return ResponseEntity.ok(papers);
    }

//    @GetMapping("/list-papers-for-PIs")
//    public ResponseEntity<List<?>> getPapersByUserIdWithPIRole(@RequestParam int userId){
//        List<PaperDTO> paperDTOs = paperService.getPaperByUserId(userId);
//        return ResponseEntity.ok(paperDTOs);
//    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<PaperDTO> uploadPaper(@RequestPart("dto") PaperDTO dto, @RequestParam("publishDate") String publishDate, @RequestParam("file") MultipartFile file) throws IOException {
        if(Objects.isNull(dto.getPriority())) {
            dto.setPriority(1);
        }
//        LocalDateTime localDateTime = LocalDateTime.parse(publishDate);
//        dto.setPublishDate(localDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDate.parse(publishDate, formatter).atStartOfDay();
        dto.setPublishDate(localDateTime);
        PaperDTO result = paperService.insertPaper(dto, file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync")
    public ResponseEntity<List<PaperDTO>> getUpdatedPapers(@RequestParam("lastSync") Long lastSyncMillis) {
        LocalDateTime lastSync = null;
        if (lastSyncMillis != null && lastSyncMillis > 0) {
            lastSync = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSyncMillis), ZoneOffset.UTC);
        }
        List<PaperDTO> results = paperService.getUpdatedPapers(lastSync);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping( "/{id}")
    public ResponseEntity<Boolean> deletePaper( @PathVariable("id") long paperId ) {
        boolean result = paperService.deletePaper(paperId);
        return ResponseEntity.ok(result);
    }

    @PostMapping( "/add-to-favourite")
    public ResponseEntity<FavoritePaperDTO> addToFavoritePapers(@RequestParam("userId") long userId , @RequestParam("paperId") long paperId ) {
        FavoritePaperDTO result = paperService.addtoFavoritePapers(userId, paperId);
        return ResponseEntity.ok(result);
    }
}
