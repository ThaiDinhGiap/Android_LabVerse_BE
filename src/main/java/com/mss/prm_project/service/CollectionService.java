package com.mss.prm_project.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.entity.CollectionMember;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.CollectionCreateRequest;
import com.mss.prm_project.model.CollectionDetailResponse;
import com.mss.prm_project.model.CollectionResponse;

import java.util.List;

public interface CollectionService {
    CollectionResponse createCollection(CollectionCreateRequest collectionCreateRequest, User user);

    List<CollectionResponse> getMyCollections(User user);

    CollectionResponse addPaperCollection(int collectionID, int paperId, User user) throws FirebaseMessagingException;

    void inviteMember(int collectionID, String invitedEmail, User user);

    CollectionMember acceptInvitation(int collectionId, User currentUser);

    CollectionDetailResponse getCollectionDetails(int collectionId, int priority, User user);

    String removePaperFromCollection(int collectionId, int paperId, User user);

    void rejectInvitation(int collectionId, User user);

}
