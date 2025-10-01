package org.example;
import java.util.ArrayList;

public class User {
    private String username;
    private volatile String password;

    public ArrayList<Book> borrowed;

    public User(String usr, String pass) {
        //
    }

    public String getUsername() {
        //return username;
        return null;
    }

    public boolean passwordValid() {
        // check null
        // meets other requirements - not equal to username, length, symbol
        return false;
    }

    public boolean passwordCorrect(String given) {
        // does given match?
        return false;
    }

    public ArrayList<Book> getBorrowed() {
        return null;
    }
}
