package org.example;
import java.util.ArrayList;

public class User {
    private String username;
    private volatile String password;

    private ArrayList<Book> borrowed;
    private ArrayList<String> notifications;

    public User(String usr, String pass) {
        this.username = usr;
        this.password = pass;
        this.borrowed = new ArrayList<Book>();
        this.notifications = new ArrayList<String>();
    }

    public String getUsername() {
        //return username;
        return this.username;
    }

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

    public boolean passwordCorrect(String given) {
        // does given match?
        return password.equals(given);
    }

    public ArrayList<Book> getBorrowed() {
        return borrowed;
    }

    public boolean addBorrowed(Book book) {
        if (borrowed.size() >= 3) {
            return false; // over or at limit
        }
        for (int i = 0; i < borrowed.size(); i++) {
            if (borrowed.get(i).getTitle().equals(book.getTitle())) {
                return false; // already in
            }
        }
        borrowed.add(book);
        return true; // book added
    }

    public boolean removeBorrowed(Book book) {
        for (int i = 0; i < borrowed.size(); i++) {
            if (borrowed.get(i).getTitle().equals(book.getTitle())) {
                borrowed.remove(i);
                return true;
            }
        }
        return false; // book not in borrowed
    }

    public void addNoti(String text) {
        this.notifications.add(text);
    }

    public boolean hasNoti() {
        return !this.notifications.isEmpty();
    }

    public String popNoti() {
        return this.notifications.removeFirst();
    }
}
