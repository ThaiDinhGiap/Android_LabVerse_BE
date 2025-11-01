package com.mss.prm_project.model;

public class ReadingListResponse {
    private int readingId;
    private String name;
    private String description;
    private int ownerUserId;
    private int paperCount;

    public ReadingListResponse() {
    }

    public ReadingListResponse(int readingId, String name, String description, int ownerUserId, int paperCount) {
        this.readingId = readingId;
        this.name = name;
        this.description = description;
        this.ownerUserId = ownerUserId;
        this.paperCount = paperCount;
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

    public int getPaperCount() {
        return paperCount;
    }

    public void setPaperCount(int paperCount) {
        this.paperCount = paperCount;
    }
}
