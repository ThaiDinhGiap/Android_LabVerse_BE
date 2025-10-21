package com.mss.prm_project.service;

import com.mss.prm_project.entity.CollectionMember;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.CollectionCreateRequest;
import com.mss.prm_project.model.CollectionDetailResponse;
import com.mss.prm_project.model.CollectionResponse;

import java.util.List;

public interface CollectionService {
    CollectionResponse createCollection(CollectionCreateRequest collectionCreateRequest, User user);
    List<CollectionResponse> getMyCollections(User user);
    CollectionResponse addPaperCollection(int collectionID, int paperId, User user);
    void inviteMember(int collectionID, String invitedEmail, User user);
    CollectionMember acceptInvitation(int collectionId, User currentUser);
    CollectionDetailResponse getCollectionDetails(int collectionId, User user);

}
