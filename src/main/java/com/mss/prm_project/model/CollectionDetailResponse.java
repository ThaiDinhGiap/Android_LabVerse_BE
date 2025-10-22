package com.mss.prm_project.model;

import lombok.Data;

import java.util.List;

@Data
public class CollectionDetailResponse {

    private int collectionId;
    private String name;
    private String ownerUsername;
    private int paperCount;
    private int memberCount;

    private List<CollectionMemberResponse> members;

    private List<SimplePaperResponse> papers;
}
