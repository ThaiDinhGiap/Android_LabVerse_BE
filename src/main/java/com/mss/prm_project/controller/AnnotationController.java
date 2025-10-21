package com.mss.prm_project.controller;

import com.mss.prm_project.dto.AnnotationDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.service.AnnotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/annotations")
public class AnnotationController {
    private final AnnotationService annotationService;

    // API chia sẻ quyền đọc cho user
    @PostMapping()
    public ResponseEntity<Set<UserDTO>> shareReaderToAnnotation(
            @RequestParam("annotationId") long annotationId,
            @RequestParam("userId") long userId) {
        Set<UserDTO> updatedReaders = annotationService.shareReaderToAnnotation(annotationId, userId);
        return ResponseEntity.ok(updatedReaders);
    }

    // API xóa quyền đọc của user
    @DeleteMapping("/{annotationId}/remove/{userId}")
    public ResponseEntity<Set<UserDTO>> removeReaderFromAnnotation(
            @PathVariable long annotationId,
            @PathVariable long userId) {
        Set<UserDTO> updatedReaders = annotationService.removeReaderFromAnnotation(annotationId, userId);
        return ResponseEntity.ok(updatedReaders);
    }

    // API lấy tất cả các Annotation mà user có quyền đọc
    @GetMapping("/readable/{userId}")
    public ResponseEntity<List<AnnotationDTO>> findAllReadableAnnotationByUserId(
            @PathVariable long userId) {
        List<AnnotationDTO> annotations = annotationService.findAllReadableAnnotationByUserId(userId);
        return ResponseEntity.ok(annotations);
    }

    // API tìm kiếm một Annotation theo ID
    @GetMapping("/{annotationId}")
    public ResponseEntity<AnnotationDTO> findAnnotationById(@PathVariable long annotationId) {
        AnnotationDTO annotation = annotationService.findAnnotationById(annotationId);
        return ResponseEntity.ok(annotation);
    }

    // API tạo mới một Annotation
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnnotationDTO> createAnnotation(@RequestParam("annotationName") String annotationName, @RequestParam("paperId") int paperId,
                                                          @RequestParam("userId") int userId, @RequestParam("file") MultipartFile file) throws IOException {
        AnnotationDTO createdAnnotation = annotationService.createAnnotation(annotationName, file, paperId, userId);
        return ResponseEntity.status(201).body(createdAnnotation);
    }

    // API xóa một Annotation theo ID
    @DeleteMapping("/{annotationId}")
    public ResponseEntity<Void> deleteAnnotation(@PathVariable long annotationId) {
        boolean success = annotationService.deleteAnnotation(annotationId);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
