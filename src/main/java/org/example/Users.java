package org.example;

import java.util.ArrayList;

public class Users {
    protected ArrayList<User> users;

    public Users() {
        users = new ArrayList<User>();
    }

    // return the user with the given username, or null if not found
    public User getUser(String usr) {
        for (User user : users) {
            if (user.getUsername().equals(usr)) {
                return user;
            }
        }
        return null;
    }

    // return the number of users in the collection
    public int getSize() {
        return users.size();
    }

    // return the list of users
    public ArrayList<User> getUsers() {
        return users;
    }

    // add a new user to the collection
    public void addUser(User user) {
        users.add(user);
    }
}
