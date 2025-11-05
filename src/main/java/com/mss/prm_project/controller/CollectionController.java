package com.mss.prm_project.controller;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.*;
import com.mss.prm_project.service.CollectionService;
import com.mss.prm_project.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final FcmService fcmService;

    @PostMapping
    public ResponseEntity<CollectionResponse> createCollection(
            @RequestBody CollectionCreateRequest  request,
            @AuthenticationPrincipal User user){
        CollectionResponse collectionResponse = collectionService.createCollection(request,user);
        return new  ResponseEntity<>(collectionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> getMyCollections(@AuthenticationPrincipal User user){
        List<CollectionResponse> collectionResponse = collectionService.getMyCollections(user);
        return ResponseEntity.status(HttpStatus.OK).body(collectionResponse);
    }

    @GetMapping("/detail")
    public ResponseEntity<CollectionDetailResponse> getCollectionDetails(
            @RequestParam("collectionId") int collectionId,
            @RequestParam(value = "priority", required = false) int priority,
            @AuthenticationPrincipal User user){
        CollectionDetailResponse collectionDetailResponse = collectionService.getCollectionDetails(collectionId, priority, user);
        return ResponseEntity.status(HttpStatus.OK).body(collectionDetailResponse);
    }

    @PostMapping("/{collectionId}/papers")
    public ResponseEntity<CollectionResponse> addPaper(
            @PathVariable int collectionId,
            @RequestBody AddPaperRequest request,
            @AuthenticationPrincipal User user) throws FirebaseMessagingException {

        System.out.println(collectionId + String.valueOf(request));
        System.out.println(request.getPaperId());

        if (request == null || request.getPaperId() == 0 || request.getPaperId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paperId is required and must be > 0");
        }
        CollectionResponse response = collectionService.addPaperCollection(
                collectionId,
                request.getPaperId(),
                user
        );
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{collectionId}/invite")
    public ResponseEntity<Void> inviteMember(
            @PathVariable int collectionId,
            @RequestBody InviteMemberRequest request,
            @AuthenticationPrincipal User user){
        collectionService.inviteMember(
                collectionId,
                request.getInvitedUserEmail(),
                user
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{collectionId}/accept")
    public ResponseEntity<Void> acceptInvitation(
           @PathVariable int collectionId,
           @AuthenticationPrincipal User user){
        collectionService.acceptInvitation(
                collectionId,
                user
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{collectionId}/papers/{paperId}")
    public ResponseEntity<ResponseObject> removePaper(
            @PathVariable int collectionId,
            @PathVariable int paperId,
            @AuthenticationPrincipal User user) throws FirebaseMessagingException { // Lấy thông tin User đang đăng nhập

        String message = collectionService.removePaperFromCollection(collectionId, paperId, user);
        fcmService.sendNotificationToToken(user.getFcmToken(), "Title", "Body");
        return ResponseEntity.ok(
                ResponseObject.builder().message(message).data(null).build()
        );
    }

    @PutMapping("/{collectionId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable int collectionId,
            @AuthenticationPrincipal User user) throws FirebaseMessagingException {
        collectionService.rejectInvitation(
                collectionId,
                user
        );
        fcmService.sendNotificationToToken(user.getFcmToken(), "Title", "Body");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
