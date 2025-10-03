package org.example;

public class Book {
    private final String title;
    private final String author;
    private boolean available;

    public Book(String title, String author){
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }

    public boolean getAvailability() {
        return available;
    }

    public void setAvailability(boolean status) {

    }
}
