package com.mss.prm_project.service;

import com.mss.prm_project.entity.ReadingList;
import com.mss.prm_project.model.ReadingListDetailResponse;
import com.mss.prm_project.model.ReadingListPaperDetailResponse;
import com.mss.prm_project.model.ReadingListResponse;

import java.util.List;

public interface ReadingListService {
    ReadingList getListIfOwner(int listId, int userId);
    ReadingListResponse createList(int ownerId, String name, String description);
    List<ReadingListResponse> getListsByOwner(int userId);
    ReadingListPaperDetailResponse getListDetails(int listId);
    ReadingListDetailResponse addPaperToList(int listId, int paperId, int userId);
    ReadingListDetailResponse removePaperFromList(int listId, int paperId, int userId);
    void deleteList(int listId, int userId);
    void addViewerToList(int listId, String invitedEmail, int ownerId);
    void removeViewerFromList(int listId, int viewerId, int ownerId);
}
