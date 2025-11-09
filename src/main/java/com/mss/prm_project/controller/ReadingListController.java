package com.mss.prm_project.controller;

import com.mss.prm_project.entity.User;

import com.mss.prm_project.model.*;
import com.mss.prm_project.service.MailService;
import com.mss.prm_project.service.ReadingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readinglists")
public class ReadingListController {

    private final ReadingListService readingListService;
    private final MailService mailService;
    // -----------------------------------------------------------------------
    // API 1: TẠO DANH SÁCH MỚI (CREATE)
    // POST /api/readinglists
    // -----------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<ReadingListResponse> createList(
            @RequestBody ReadingListCreationRequest request,
            @AuthenticationPrincipal User user) {

        ReadingListResponse listResponse = readingListService.createList(
                user.getUserId(),
                request.getName(),
                request.getDescription()
        );
        return new ResponseEntity<>(listResponse, HttpStatus.CREATED); // 201 Created
    }

    // -----------------------------------------------------------------------
    // API 2: LẤY DANH SÁCH CỦA TÔI
    // GET /api/readinglists
    // -----------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ReadingListResponse>> getMyLists(@AuthenticationPrincipal User user) {

        List<ReadingListResponse> listResponse = readingListService.getListsByOwner(user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(listResponse); // 200 OK
    }

    // -----------------------------------------------------------------------
    // API 3: LẤY CHI TIẾT DANH SÁCH
    // GET /api/readinglists/{listId}
    // -----------------------------------------------------------------------
    @GetMapping("/{listId}")
    public ResponseEntity<ReadingListPaperDetailResponse> getListDetails(
            @PathVariable int listId,
            @AuthenticationPrincipal User user) {
        ReadingListPaperDetailResponse detailResponse = readingListService.getListDetails(listId);
        return ResponseEntity.status(HttpStatus.OK).body(detailResponse); // 200 OK
    }

    // -----------------------------------------------------------------------
    // API 4: THÊM PAPER VÀO LIST
    // POST /api/readinglists/{listId}/papers
    // -----------------------------------------------------------------------
    @PostMapping("/{listId}/papers")
    public ResponseEntity<ReadingListDetailResponse> addPaper(
            @PathVariable int listId,
            @RequestBody AddPaperRequest request,
            @AuthenticationPrincipal User user) {

        ReadingListDetailResponse response = readingListService.addPaperToList(
                listId,
                request.getPaperId(), // Lấy ID từ Request Body
                user.getUserId()
        );
        // Trả về 200 OK (Cập nhật thành công)
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // -----------------------------------------------------------------------
    // API 5: XÓA PAPER KHỎI LIST (UPDATE - Remove)
    // DELETE /api/readinglists/{listId}/papers/{paperId}
    // -----------------------------------------------------------------------
    @DeleteMapping("/{listId}/papers/{paperId}")
    public ResponseEntity<ResponseObject> removePaper(
            @PathVariable int listId,
            @PathVariable int paperId,
            @AuthenticationPrincipal User user) {

        // Service thực hiện xóa và trả về DTO đã cập nhật
        ReadingListDetailResponse updatedList = readingListService.removePaperFromList(
                listId,
                paperId,
                user.getUserId()
        );

        // Trả về ResponseObject theo phong cách mẫu của bạn
        String message = "Paper (ID: " + paperId + ") successfully removed from reading list (ID: " + listId + ").";
        return ResponseEntity.ok(
                ResponseObject.builder().message(message).data(updatedList).build()
        );
    }

    // -----------------------------------------------------------------------
    // API 6: XÓA TOÀN BỘ DANH SÁCH (DELETE)
    // DELETE /api/readinglists/{listId}
    // -----------------------------------------------------------------------
    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable int listId, @AuthenticationPrincipal User user) {

        readingListService.deleteList(listId, user.getUserId());

        // Trả về 204 No Content
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listId}/viewers")
    public ResponseEntity<ResponseObject> addViewer(
            @PathVariable int listId,
            @RequestBody InviteMemberRequest request,
            @AuthenticationPrincipal User user) {

        // Gọi Service với Email
        readingListService.addViewerToList(
                listId,
                request.getInvitedUserEmail(), // Truyền Email
                user.getUserId()           // ID của chủ sở hữu
        );
        String message = "User with email '" + request.getInvitedUserEmail() + "' successfully added as a viewer to reading list. You can login to Labverse and access Reading List to view Papers";
        mailService.sendNotification(request.getInvitedUserEmail(),message);

        return ResponseEntity.ok(
                ResponseObject.builder().message(message).data(null).build()
        );
    }

    // -----------------------------------------------------------------------
    // API 8: XÓA NGƯỜI XEM (REMOVE VIEWER)
    // DELETE /api/readinglists/{listId}/viewers/{viewerId}
    // -----------------------------------------------------------------------
    @DeleteMapping("/{listId}/viewers/{viewerId}")
    public ResponseEntity<ResponseObject> removeViewer(
            @PathVariable int listId,
            @PathVariable int viewerId,
            @AuthenticationPrincipal User user) {

        // Service sẽ kiểm tra quyền sở hữu (User phải là owner)
        readingListService.removeViewerFromList(
                listId,
                viewerId, // ID của người xem cần xóa
                user.getUserId() // ID của chủ sở hữu
        );

        String message = "Viewer successfully removed from reading list .";
        return ResponseEntity.ok(
                ResponseObject.builder().message(message).data(null).build()
        );
    }
}