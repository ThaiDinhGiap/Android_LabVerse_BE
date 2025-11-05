package com.mss.prm_project.model;

import com.mss.prm_project.dto.PaperDTO;

import java.util.List;

public class ReadingListDetailResponse {
    private int readingId;
    private String name;
    private String description;
    private int ownerUserId;
    private String ownerUsername;
    private int paperCount;
    private List<PaperResponse> papers;

    public ReadingListDetailResponse() {
    }

    public ReadingListDetailResponse(int readingId, String name, String description, int ownerUserId, String ownerUsername, int paperCount, List<PaperResponse> papers) {
        this.readingId = readingId;
        this.name = name;
        this.description = description;
        this.ownerUserId = ownerUserId;
        this.ownerUsername = ownerUsername;
        this.paperCount = paperCount;
        this.papers = papers;
    }

    public int getReadingId() {
        return readingId;
    }

    public void setReadingId(int readingId) {
        this.readingId = readingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
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

    public List<PaperResponse> getPapers() {
        return papers;
    }

    public void setPapers(List<PaperResponse> papers) {
        this.papers = papers;
    }
}
