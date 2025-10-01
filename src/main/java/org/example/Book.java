package org.example;

public class Book {
    String title;
    String author;

    public Book(String title, String author){
        this.title = title;
        this.author = author;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }
}
