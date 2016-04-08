package com.forever.kimoo.popularmovies;

/**
 * Created by KiMoo on 24/01/2016.
 */
public class Review {

    private String id;
    private String author;
    private String content;

    public Review() {

    }

    public Review(String id,String author,String content) {
        this.setId(id);
        this.setAuthor(author);
        this.setContent(content);
    }

    public String getId() { return id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
