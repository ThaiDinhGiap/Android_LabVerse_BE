package com.mss.prm_project.model;


public class PaperResponse {
    int paperId;

    String title;

    String author;

    String journal;

    public PaperResponse() {
    }

    public PaperResponse(int paperId, String title, String author, String journal) {
        this.paperId = paperId;
        this.title = title;
        this.author = author;
        this.journal = journal;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }
}
