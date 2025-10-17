package org.example;

import java.util.ArrayList;

/**
 * Catalogue maintains a collection of Book objects.
 * Responsibilities:
 * - Add books (by title/author or by Book instance)
 * - Retrieve books by title or index
 * - Report catalogue size and expose the underlying list
 */
public class Catalogue {
    // Internal list storing books in insertion order
    private ArrayList<Book> catalogue;

    public Catalogue() {
        // Initialize empty catalogue
        catalogue = new ArrayList<>();
    }

    /**
     * Find a book by exact title match.
     * Iterates through the catalogue and returns the first Book whose title equals the supplied title.
     * @param title title to search for
     * @return matching Book or null if not found
     */
    public Book getBook(String title) {
        for (Book book : catalogue) {
            // Use String.equals to compare titles for exact match
            if (title.equals(book.getTitle())) {
                return book;
            }
        }
        return null;
    }

    /**
     * Retrieve a book by index with bounds checking.
     * @param index zero-based index
     * @return Book at index or null if index is out of range
     */
    public Book getBook(int index) {
        // Guard against invalid indices
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return catalogue.get(index);
    }

    /**
     * Current number of books in the catalogue.
     * @return size of the catalogue
     */
    public int getSize() {
        return catalogue.size();
    }

    /**
     * Expose the internal list.
     * @return underlying ArrayList of books
     */
    public ArrayList<Book> getCatalogue() {
        return catalogue;
    }

    /**
     * Create and add a new Book with the given title and author.
     * @param title book title
     * @param author book author
     */
    public void addBook(String title, String author) {
        catalogue.add(new Book(title, author));
    }

    /**
     * Add an existing Book instance to the catalogue.
     * @param book Book object to add
     */
    public void addBook(Book book) {
        catalogue.add(book);
    }
}
