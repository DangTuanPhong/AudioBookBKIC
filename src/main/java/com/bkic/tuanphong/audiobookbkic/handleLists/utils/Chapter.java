package com.bkic.tuanphong.audiobookbkic.handleLists.utils;


public class Chapter {
    private int id;
    private String chapterTitle;
    private String fileUrl;
    private int length;
    private int bookId;
    private int status;
    private String bookTitle;

    public Chapter(int id, String title, String bookTitle,String fileUrl, int length, int bookId) {
        this.id = id;
        this.chapterTitle = title;
        this.fileUrl = fileUrl;
        this.length = length;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
    }

    public Chapter() {
    }

    public int getId() {
        return id;
    }

    public Chapter(String title) {
        this.chapterTitle = title;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return chapterTitle;
    }

    public void setTitle(String title) {
        this.chapterTitle = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getBookId() {
        return bookId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
