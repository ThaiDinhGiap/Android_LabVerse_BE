package com.mss.prm_project.model;

import lombok.Data;

import java.util.List;


public class CollectionDetailResponse {

    private int collectionId;
    private String name;
    private String ownerUsername;
    private int paperCount;
    private int memberCount;

    private List<CollectionMemberResponse> members;

    private List<SimplePaperResponse> papers;

    public CollectionDetailResponse() {
    }

    public CollectionDetailResponse(int collectionId, String name, String ownerUsername, int paperCount, int memberCount, List<CollectionMemberResponse> members, List<SimplePaperResponse> papers) {
        this.collectionId = collectionId;
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.paperCount = paperCount;
        this.memberCount = memberCount;
        this.members = members;
        this.papers = papers;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public int getPaperCount() {
        return paperCount;
    }

    public void setPaperCount(int paperCount) {
        this.paperCount = paperCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<CollectionMemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<CollectionMemberResponse> members) {
        this.members = members;
    }

    public List<SimplePaperResponse> getPapers() {
        return papers;
    }

    public void setPapers(List<SimplePaperResponse> papers) {
        this.papers = papers;
    }
}
