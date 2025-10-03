package org.example;

import java.util.ArrayList;

public class Catalogue {
    private ArrayList<Book> catalogue;

    public Catalogue() {
        catalogue = new ArrayList<Book>();

    }

    public void addBook(String title, String author) {
        catalogue.add(new Book(title, author));
    }

    public Book getBook(String title) {
        for (Book book : catalogue) {
            if (title.equals(book.getTitle())) {
                return book;
            }
        }
        return null;
    }

    public Book getBook(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return catalogue.get(index);
    }

    public int getSize() {
        return catalogue.size();
    }

    public ArrayList<Book> getCatalogue() {
        return catalogue;
    }
}
