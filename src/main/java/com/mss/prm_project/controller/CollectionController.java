package com.mss.prm_project.controller;


import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.*;
import com.mss.prm_project.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;

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

    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionDetailResponse> getCollectionDetails(
            @PathVariable int collectionId,
            @AuthenticationPrincipal User user){
        CollectionDetailResponse collectionDetailResponse = collectionService.getCollectionDetails(collectionId, user);
        return ResponseEntity.status(HttpStatus.OK).body(collectionDetailResponse);
    }

    @PostMapping("/{collectionId}/papers")
    public ResponseEntity<CollectionResponse> addPaper(
            @PathVariable int collectionId,
            @RequestBody AddPaperRequest request,
            @AuthenticationPrincipal User user){

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
}
