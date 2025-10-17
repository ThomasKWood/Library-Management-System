package org.example;
import java.util.ArrayList;

public class User {
    // user's login name
    private String username;
    private volatile String password;

    // list of books the user currently has borrowed
    private ArrayList<Book> borrowed;
    // list of notification messages (titles) for the user
    private ArrayList<String> notifications;

    // construct a new user with username and password, initialize lists
    public User(String usr, String pass) {
        this.username = usr;
        this.password = pass;
        this.borrowed = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    // return the user's username
    public String getUsername() {
        //return username;
        return this.username;
    }

    // return the list of borrowed books
    public ArrayList<Book> getBorrowed() {
        return borrowed;
    }

    // add a notification message for the user
    public void addNotification(String text) {
        this.notifications.add(text);
    }

    // check whether any notifications are queued for the user
    public boolean hasNotification() {
        return !this.notifications.isEmpty();
    }

    // remove and return the first notification (assumes first element represents oldest)
    public String popNotification() {
        return this.notifications.removeFirst();
    }

    // attempt to add a borrowed book to the user's list with basic guards
    public void addBorrowed(Book book) {
        // enforce borrow limit of 3
        if (borrowed.size() >= 3) {
            return; // over or at limit
        }
        // avoid adding duplicate titles
        for (Book value : borrowed) {
            if (value.getTitle().equals(book.getTitle())) {
                return; // already in
            }
        }
        borrowed.add(book);
    }

    // basic password validity checks: non-null, length, not equal to username
    public boolean passwordValid() {
        // check null
        // meets other requirements - not equal to username, length, symbol
        if (password == null) {
            return false;
        } else {
            if (password.length() < 4) {
                return false;
            }
            if (password.equals(username)) {
                return false;
            }
        }
        return true;
    }

    // check whether a provided password matches the stored password
    public boolean passwordCorrect(String given) {
        // does given match?
        return password.equals(given);
    }
}
