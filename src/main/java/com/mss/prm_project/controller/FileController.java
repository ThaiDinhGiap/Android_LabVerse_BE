//package com.mss.prm_project.controller;
//
//import com.mss.prm_project.service.serviceimpl.S3ServiceV2;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/file")
//public class FileController {
//    private final S3ServiceV2 s3Service;
//
////    @PostMapping("/upload")
////    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
////        try {
////            //Upload file lên S3
////            String key = s3Service.uploadFile(file);
////
////            //Tạo URL truy cập file (tùy vào region + bucket)
////            String fileUrl = "https://" + s3Service.getBucketName() + ".s3.amazonaws.com/" + key;
////
////            //Trả phản hồi JSON
////            Map<String, String> response = new HashMap<>();
////            response.put("message", "Upload thành công!");
////            response.put("key", key);
////            response.put("url", fileUrl);
////
////            return ResponseEntity.ok(response);
////
////        } catch (IOException e) {
////            e.printStackTrace();
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "Lỗi khi upload file: " + e.getMessage()));
////        }
////    }
//@PostMapping("/upload")
//public ResponseEntity<?> uploadPdfWithMetaData(
//        @RequestPart("file") MultipartFile file,
//        @RequestPart("title") String title,
//        @RequestPart("author") String author,
//        @RequestPart("journal") String journal,
//        @RequestPart("publisher") String publisher,
//        @RequestPart("publishDate") String publishDate,
//        @RequestPart("priority") String priority
//) {
//    try {
//        // 🧾 Log tất cả thông tin nhận được
//        log.info("=== Nhận được yêu cầu upload file PDF ===");
//        log.info("File name      : {}", file.getOriginalFilename());
//        log.info("File size      : {} bytes", file.getSize());
//        log.info("MIME type      : {}", file.getContentType());
//        log.info("Title          : {}", title);
//        log.info("Author         : {}", author);
//        log.info("Journal        : {}", journal);
//        log.info("Publisher      : {}", publisher);
//        log.info("Publish Date   : {}", publishDate);
//        log.info("Priority       : {}", priority);
//        log.info("==========================================");
//
//        // ✅ Trả phản hồi đơn giản
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Dữ liệu đã được nhận thành công");
//        response.put("fileName", file.getOriginalFilename());
//        response.put("fileSize", file.getSize());
//        response.put("metaData", Map.of(
//                "title", title,
//                "author", author,
//                "journal", journal,
//                "publisher", publisher,
//                "publishDate", publishDate,
//                "priority", priority
//        ));
//
//        return ResponseEntity.ok(response);
//
//    } catch (Exception e) {
//        log.error("❌ Lỗi khi xử lý upload: {}", e.getMessage(), e);
//        return ResponseEntity.internalServerError()
//                .body(Map.of("error", e.getMessage()));
//    }
//}
//}
