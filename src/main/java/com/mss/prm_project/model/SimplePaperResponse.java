package com.mss.prm_project.model;



public class SimplePaperResponse {
    private int paperId;
    private String title;

    private String author;

    public SimplePaperResponse() {
    }

    public SimplePaperResponse(int paperId, String title, String author) {
        this.paperId = paperId;
        this.title = title;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
